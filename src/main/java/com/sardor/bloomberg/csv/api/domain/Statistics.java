package com.sardor.bloomberg.csv.api.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by sardor.
 * Entity class bound to DB table statistics that stores
 * valid ordering deals statistic per currency
 */
@Entity
@Table(name = "statistics")
public class Statistics implements Serializable {
    private static final long serialVersionUID = -7683824390291578774L;

    @Id
    @Column(name = "iso_code")
    private String isoCode;

    @Column
    private Long count;

    public Statistics() {
    }

    public Statistics(String isoCode, Long count) {
        this.isoCode = isoCode;
        this.count = count;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
