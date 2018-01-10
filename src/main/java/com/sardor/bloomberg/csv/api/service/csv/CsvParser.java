package com.sardor.bloomberg.csv.api.service.csv;

import com.sardor.bloomberg.csv.api.domain.DealsHelper;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by sardor.
 */
public interface CsvParser {
    DealsHelper parseNext(Reader reader) throws IOException;
}
