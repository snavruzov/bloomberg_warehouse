package com.sardor.bloomberg.csv.api.service.csv;

import com.sardor.bloomberg.csv.api.domain.DealsHelper;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * Created by sardor.
 */
public class CsvReader implements Closeable, Iterable<DealsHelper>{

    private BufferedReader br;
    public int recordNumber = 0;
    public DealsHelper deals;

    private CsvParser parser;
    private int skipLines;

    /**
     * The default line to start reading.
     */
    public static final int DEFAULT_SKIP_LINES = 1;

    /**
     * Constructs CsvReader with supplied separator and quote char.
     *
     * @param reader    the reader to an underlying CSV source.
     * @param line      the line number to skip for start reading
     * @param csvParser the parser to use to parse input
     */
    public CsvReader(Reader reader, int line, CsvParser csvParser) {
        this.br = (reader instanceof BufferedReader ?
                (BufferedReader) reader : new BufferedReader(reader));
        this.skipLines = line;
        this.parser = csvParser;
    }

    /**
     * Reads the next line from the buffer and maps to a helper class.
     *
     * @return a {@link DealsHelper}.
     * @throws IOException if bad things happen during the read
     */
    protected DealsHelper readNext() throws IOException {
        try {
            while (skipLines > 0) {
                if(br.readLine()==null) {
                    // if we reacher EOF, then consider all lines skipped
                    skipLines = 0;
                } else {
                    skipLines--;
                }
            }

            DealsHelper next = parser.parseNext(br);
            if (next == null) {
                return null;
            }
            recordNumber++;
            this.deals = next;
            if(deals.isError()){
                deals.setErrorLineNumber(recordNumber);
            }
            return deals;

        } catch (Exception re) {
            throw new IllegalArgumentException(re);
        }
    }


    /**
     * Closes the underlying reader.
     *
     * @throws IOException if the close fails
     */
    public void close() throws IOException {
        br.close();
    }

    public Iterator<DealsHelper> iterator() {
        try {
            return new CsvIterator(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
