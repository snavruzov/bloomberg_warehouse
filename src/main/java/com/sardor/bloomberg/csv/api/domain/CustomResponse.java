package com.sardor.bloomberg.csv.api.domain;

import java.io.Serializable;

/**
 * Created by sardor.
 * Custom class to build response data that Spring web serialize to JSON form
 */
public class CustomResponse implements Serializable {

    private static final long serialVersionUID = 2263535118591178237L;

    private String code;
    private String message;
    private CsvResult result;
    private boolean error;

    public CustomResponse(String code, String message, CsvResult result, boolean error) {
        this.code = code;
        this.message = message;
        this.result = result;
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CsvResult getResult() {
        return result;
    }

    public void setResult(CsvResult result) {
        this.result = result;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
