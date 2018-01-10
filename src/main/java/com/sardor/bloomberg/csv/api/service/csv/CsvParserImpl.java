package com.sardor.bloomberg.csv.api.service.csv;

import com.sardor.bloomberg.csv.api.domain.DealsHelper;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by sardor.
 */
public class CsvParserImpl implements CsvParser{
    final char separator;
    final char quotechar;
    final boolean allowUnixTime;
    final boolean trimWhiteSpace;           // if true, trim leading/trailing white space from tokens
    final int fields_number;
    final SimpleDateFormat formatter;


    static final int INITIAL_READ_SIZE = 128;

    public CsvParserImpl() {
        separator = CsvUtils.DEFAULT_SEPARATOR;
        quotechar = CsvUtils.DEFAULT_QUOTE_CHAR;
        trimWhiteSpace = CsvUtils.DEFAULT_TRIM_WS;
        fields_number = CsvUtils.DEFAULT_FIELDS_NUMBER;
        allowUnixTime = CsvUtils.ALLOW_UNIX_TIME;
        formatter = new SimpleDateFormat(CsvUtils.DATE_FORMAT);
    }

    /**
     * Constructor with all options settable. Unless you want the default
     * behavior, use the Builder to set the options you want.
     *
     * @param separator single char that separates values in the list
     * @param quotechar single char that is used to quote values
     * @param trimWhiteSpace trims leading and trailing whitespace of each token
     * @param fields_number fields total number to map values to the corresponding object
     * @param allowUnixTime allow unix time format for date valued fields
     */
    public CsvParserImpl(final char separator, final char quotechar,
                              final boolean trimWhiteSpace,
                              final int fields_number,
                              final boolean allowUnixTime, SimpleDateFormat formatter) {
        this.separator = separator;
        this.quotechar = quotechar;
        this.allowUnixTime = allowUnixTime;
        this.trimWhiteSpace = trimWhiteSpace;
        this.fields_number = fields_number;
        this.formatter = formatter;

        checkInvariants();
    }

    private void checkInvariants() {
        if (CsvUtils.anyCharactersAreTheSame(separator, quotechar)) {
            throw new UnsupportedOperationException("The separator, quote characters must be different!");
        }
        if (separator == CsvUtils.NULL_CHARACTER) {
            throw new UnsupportedOperationException("The separator character must be defined!");
        }
        if (quotechar == CsvUtils.NULL_CHARACTER) {
            throw new UnsupportedOperationException("The quote character must be defined");
        }
    }

    // keep track of mutable States
    static final class State {

        boolean inQuotes = false;

        private void quoteFound() {
            inQuotes = !inQuotes;
        }

        public void reset() {
            inQuotes = false;
        }
    }

    /**
     * Parses a record (a single row of fields) as defined by the presence of LF
     * or CRLF. If a CR or CRLF is detected inside a quoted value then the value
     * returned will contain them and the parser will continue to look for the
     * real record ending. 
     *
     * @param reader the Reader get our data from
     * @return parsed tokens as List of Strings
     * @throws java.io.IOException
     */
    public DealsHelper parseNext(Reader reader) throws IOException {

        // check EOF first
        int r = reader.read();
        if (r == -1) {
            return null;
        }

        final StringBuilder sb = new StringBuilder(INITIAL_READ_SIZE);
        final StringBuilder originalLine = new StringBuilder(INITIAL_READ_SIZE);
        final List<String> toks = new LinkedList<>();
        final CvsMapping mapper = new CvsMapping(allowUnixTime, formatter);
        final State state = new State();

        while (r != -1) {
            originalLine.append((char) r);

            if (r == '\n'){
                break;
            } else if(r == '\r'){
                if ((r = reader.read()) == '\n') {
                    // END OF RECORD
                    break;
                } else {
                    handleRegular(sb, '\r');
                    continue;
                }
            } else if (isQuoteChar(r)) {

                if (state.inQuotes) {
                    if (isQuoteChar(r = reader.read())) {
                        // then consume and follow usual flow
                        sb.append((char) r);
                    } else {
                        // HANDLE QUOTE AND (a) BREAK IF EOF
                        // OR (b) START AT TOP WITH OBTAINED NEXT CHAR
                        handleQuote(state, sb);
                        continue;
                    }
                } else {
                    handleQuote(state, sb);
                }
            } else if (!state.inQuotes) {
                if (r == separator) {
                    toks.add(handleEndOfToken(sb));

                } else {
                    handleRegular(sb, (char) r);
                }
            } else {
                handleRegular(sb, (char) r);
            }
            r = reader.read();
        }

        toks.add(handleEndOfToken(sb));
        mapper.mapToDeal(toks, originalLine.toString());
        return mapper.getDeals();
    }

    /* --------------------------------- */
    /* ---[ internal helper methods ]--- */
    /* --------------------------------- */

    boolean isQuoteChar(int c) {
        // if the quotechar is set to the CsvUtils.NULL_CHAR then it shouldn't
        // match anything => nothing is the quotechar
        return c == quotechar && quotechar != CsvUtils.NULL_CHARACTER;
    }

    String handleEndOfToken(StringBuilder sb) {
        String tok = trim(sb);
        sb.setLength(0);
        return tok;
    }

    void appendRegularChar(StringBuilder sb, char c) {
        sb.append(c);
    }

    void handleRegular(StringBuilder sb, char c) {
        appendRegularChar(sb, c);
    }

    void handleQuote(State state, StringBuilder sb) {
        sb.append(quotechar);
        state.quoteFound();
    }

    String trim(StringBuilder sb) {
        int left = 0;
        int right = sb.length() - 1;


        if (trimWhiteSpace) {
            int[] indexes = CsvUtils.idxTrimSpaces(sb, left, right);
            left = indexes[0];
            right = indexes[1];

            indexes = CsvUtils.idxTrimEdgeQuotes(sb, left, right, quotechar);
            left = indexes[0];
            right = indexes[1];

            indexes = CsvUtils.idxTrimSpaces(sb, left, right);
            left = indexes[0];
            right = indexes[1];
        } else {
            CsvUtils.pluckOuterQuotes(sb, left, right, quotechar);
            left = 0;
            right = sb.length() - 1;
        }


        return sb.substring(left, right + 1);

    }
}
