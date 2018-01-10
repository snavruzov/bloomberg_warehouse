package com.sardor.bloomberg.csv.api.service.csv;

import com.sardor.bloomberg.csv.api.domain.DealsHelper;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by sardor.
 */
public class CsvIterator implements Iterator<DealsHelper> {
    private CsvReader reader;
    private DealsHelper nextLine;

    public CsvIterator(CsvReader reader) throws IOException {
        this.reader = reader;
        nextLine = reader.readNext();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("We are reading CSV only.");
    }

    @Override
    public boolean hasNext() {
        return nextLine!=null;
    }

    @Override
    public DealsHelper next() {
        DealsHelper tmp = nextLine;
        try {
            nextLine = reader.readNext();
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        return tmp;
    }
}
