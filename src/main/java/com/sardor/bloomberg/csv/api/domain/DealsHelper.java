package com.sardor.bloomberg.csv.api.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Helper class that track the data validity and post-map to the entity classes accordingly
 * {@link Deals} and {@link Broken}
 * Created by sardor.
 */

public class DealsHelper {
    private String uid;
    private String fromCurrency;
    private String toCurrency;
    private Date timestamp;
    private Double amount;
    private String originalLine;
    private boolean isError  = false;
    private long errorLineNumber;
    private boolean allowUnixTime;
    private SimpleDateFormat formatter;
    private List<String> toks;

    public DealsHelper() {
    }

    /**
     * Constructor {@link DealsHelper} with supplied certain fields params
     * @param uid Deal Unique Id
     *
     */
    public DealsHelper(String uid, String fromCurrency,
                       String toCurrency, String timestamp,
                       String amount, String originalLine,
                       boolean allowUnixTime, List<String> toks, SimpleDateFormat formatter) {
        this.allowUnixTime = allowUnixTime;
        this.formatter = formatter;
        this.toks = toks;

        this.uid = validateString(uid);
        this.fromCurrency = validateISO4217(fromCurrency);
        this.toCurrency = validateISO4217(toCurrency);
        this.amount = validateDouble(amount);
        this.timestamp = validateTimestamp(timestamp);
        this.originalLine = originalLine
                .replace("\r","")
                .replace("\n","");

    }

    
    public DealsHelper(boolean isError, String origLine) {
        this.isError = isError;
        this.originalLine = origLine
                .replace("\r","")
                .replace("\n","");
    }

    public List<String> getToks() {
        return toks;
    }

    public void setToks(List<String> toks) {
        this.toks = toks;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public long getErrorLineNumber() {
        return errorLineNumber;
    }

    public void setErrorLineNumber(long errorLineNumber) {
        this.errorLineNumber = errorLineNumber;
    }

    public String getOriginalLine() {
        return originalLine;
    }

    public void setOriginalLine(String originalLine) {
        this.originalLine = originalLine;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    private String validateString(String field) {
        isError = isError || field.isEmpty();
        return field;
    }

    private Double validateDouble(String field) {
        try{
            return Double.valueOf(field);
        } catch (NumberFormatException e){
            isError = true;
            return -1.0;
        }
    }

    private String validateISO4217(String field) {
        this.isError = isError || field.isEmpty() || field.length()!=3
                || !Pattern.matches("^[A-Za-z]{3}$", field);
        return field;
    }


    private Date validateTimestamp(String field) {
        //StopWatch watch = new StopWatch();
        //watch.start();
        Date date = new Date();
        boolean errFormat = true;
        if(allowUnixTime && !field.isEmpty()){
            errFormat = isError || !Pattern.matches("^[0-9]{10}$", field);
        } else if(!isError && !field.isEmpty()) {
            try {
                date = formatter.parse(field);
                errFormat = false;
            } catch (ParseException ignored) {
            }
        }

        isError = isError || errFormat || field.isEmpty();
        //watch.stop();
        //System.out.println(watch.prettyPrint());
        return date;
    }

    @Override
    public String toString() {
        return "[" +
                "uid='" + uid + '\'' +
                ", fromISOCurrency='" + fromCurrency + '\'' +
                ", toISOCurrency='" + toCurrency + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", amount=" + amount +
                ", cvsLineNumber=" + errorLineNumber +
                ", origLine=" + originalLine +
                ", isError=" + isError +
                ']';
    }
}
