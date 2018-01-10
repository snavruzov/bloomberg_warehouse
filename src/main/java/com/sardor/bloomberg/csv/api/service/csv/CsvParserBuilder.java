package com.sardor.bloomberg.csv.api.service.csv;

import java.text.SimpleDateFormat;

/**
 * Created by sardor.
 */
public class CsvParserBuilder {
    char separator = CsvUtils.DEFAULT_SEPARATOR;
    char quoteChar = CsvUtils.DEFAULT_QUOTE_CHAR;
    boolean trimWhitespace = CsvUtils.DEFAULT_TRIM_WS;
    boolean allowUnixTime = CsvUtils.ALLOW_UNIX_TIME;
    SimpleDateFormat formatter;

    public CsvParserBuilder separator(final char separator) {
        this.separator = separator;
        return this;
    }

    public CsvParserBuilder quoteChar(final char quoteChar) {
        this.quoteChar = quoteChar;
        return this;
    }

    public CsvParserBuilder trimWhitespace(boolean trim) {
        this.trimWhitespace = trim;
        return this;
    }

    public CsvParserBuilder allowUnixTime(boolean unixTime) {
        this.allowUnixTime = unixTime;
        return this;
    }

    public CsvParserBuilder formatter(SimpleDateFormat formatter) {
        this.formatter = formatter;
        return this;
    }

    /**
     * Constructs Parser
     */
    public CsvParser build() {
        return new CsvParserImpl(
                separator,
                quoteChar,
                trimWhitespace,
                CsvUtils.DEFAULT_FIELDS_NUMBER,
                allowUnixTime,
                formatter);
    }
}
