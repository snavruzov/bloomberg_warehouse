package com.sardor.bloomberg.csv.api.service;

import com.sardor.bloomberg.csv.api.domain.CsvResult;
import com.sardor.bloomberg.csv.api.domain.Deals;
import com.sardor.bloomberg.csv.api.domain.References;
import com.sardor.bloomberg.csv.api.service.csv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.Closeable;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by sardor.
 */

public class CsvService implements Closeable {
    private final Logger logger = LoggerFactory.getLogger(CsvService.class);

    private final String filePath;
    private final char separator;
    private final char quoteChar;
    private final boolean allowUnixTime;
    private final boolean skipHeader;
    private final String dateFormat;
    private final DealService service;

    public CsvService(String filePath, char separator, char quoteChar, boolean allowUnixTime, boolean skipHeader, String dateFormat, DealService service) {
        this.filePath = filePath;
        this.separator = separator;
        this.quoteChar = quoteChar;
        this.allowUnixTime = allowUnixTime;
        this.skipHeader = skipHeader;
        this.dateFormat = dateFormat;
        this.service = service;
    }

    public final CsvResult parseCsv(References references) throws Exception {

        final SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        CsvResult result = new CsvResult();
        FileReader fr = new FileReader(filePath);
        CsvParser p = new CsvParserBuilder().
                separator(separator).
                quoteChar(quoteChar).
                trimWhitespace(true).
                allowUnixTime(allowUnixTime).
                formatter(formatter).
                build();
        CsvReader csvr = new CsvReaderBuilder(fr).
                csvParser(p).
                skipLines(skipHeader ? 1 : 0).
                build();

        String csvDealsfile = "/tmp/"+System.currentTimeMillis()+"d.csv";
        String csvBrokenfile = "/tmp/"+System.currentTimeMillis()+"b.csv";
        FileWriter writerDeals = new FileWriter(csvDealsfile);
        FileWriter writerBrnDeals = new FileWriter(csvBrokenfile);

        StopWatch watch = new StopWatch("parseCsv");
        watch.start();
        while (csvr.iterator().hasNext()) {
            if (csvr.deals.isError()) {
                result.invalidDeals++;
                CsvUtils.writeLine(writerBrnDeals,
                        Arrays.asList("\""+csvr.deals.getOriginalLine()+"\""
                                ,references.getId()+""
                                ,csvr.deals.getErrorLineNumber()+""));
            } else {
                result.validDeals++;
                long cnt = result.orderingDealsCount.getOrDefault(
                        csvr.deals.getFromCurrency(), 0L);
                result.orderingDealsCount.put(
                        csvr.deals.getFromCurrency(), cnt + 1);
                csvr.deals.getToks().add(references.getId()+"");
                CsvUtils.writeLine(writerDeals, csvr.deals.getToks());
            }
        }

        writerBrnDeals.flush();
        writerBrnDeals.close();

        writerDeals.flush();
        writerDeals.close();


        if(result.validDeals>0){
            saveDeals(csvDealsfile);
        }
        if(result.invalidDeals>0){
            saveBrokenDeals(csvBrokenfile);
        }

        csvr.close();

        watch.stop();
        logger.info(watch.prettyPrint());
        return result;
    }

    private void saveDeals(String path){
        this.service.saveDealNative(path);
    }

    private void saveBrokenDeals(String path){
        this.service.saveBrokenNative(path);
    }

    @Override
    public void close() throws IOException {
    }
}
