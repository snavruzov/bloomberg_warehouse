package com.sardor.bloomberg.csv.api.service.csv;

import com.sardor.bloomberg.csv.api.domain.DealsHelper;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Mapping helper class to map Csv values to the {@link DealsHelper} object
 * Created by sardor.
 */
public class CvsMapping {

    private DealsHelper deals;
    private boolean allowUnixTime;
    private SimpleDateFormat formatter;

    public CvsMapping(){}

    public CvsMapping(boolean allowUnixTime){
        this.allowUnixTime = allowUnixTime;
    }

    public CvsMapping(boolean allowUnixTime, SimpleDateFormat formatter){
        this.allowUnixTime = allowUnixTime;
        this.formatter = formatter;
    }

    /**
     * Mapping to the {@link DealsHelper} class considering a passed List values validity
     * @param toks list of elements of the parsed CSV file line
     * @param origLine original CSV line, not formatted
     */

    public void mapToDeal(List<String> toks, String origLine) {
        int fieldsCount = CsvUtils.DEFAULT_FIELDS_NUMBER;
        if(toks.size() == fieldsCount) {
            this.deals = new DealsHelper(toks.get(0),
                    toks.get(1),
                    toks.get(2),
                    toks.get(3),
                    toks.get(4), origLine, allowUnixTime, toks, formatter);
        } else {
            this.deals = new DealsHelper(true, origLine);
        }
    }

    public DealsHelper getDeals() {
        return deals;
    }
}
