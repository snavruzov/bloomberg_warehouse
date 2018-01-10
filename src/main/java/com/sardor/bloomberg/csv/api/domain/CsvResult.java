package com.sardor.bloomberg.csv.api.domain;

import java.io.Serializable;
import java.util.*;

/**
 * Created by sardor.
 * Statistics per processed file
 */
public class CsvResult implements Serializable {
    private static final long serialVersionUID = 8009399121014965693L;

    public int validDeals = 0;
    public int invalidDeals = 0;
    public long benchmark = 0;
    public Map<String, Long> orderingDealsCount;

    public CsvResult() {
        orderingDealsCount = new HashMap<>();
    }
}
