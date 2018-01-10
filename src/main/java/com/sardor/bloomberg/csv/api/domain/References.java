package com.sardor.bloomberg.csv.api.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by sardor.
 * Entity class bound to DB table fileinfo that stores uploaded file information
 */
@Entity
@Table(name = "fileinfo")
public class References implements Serializable {

    private static final long serialVersionUID = -6174488473120863329L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ref_id")
    private Integer id;

    @Column
    private String file_name;

    @Column
    private String check_sum;

    public References() {
    }

    public References(String file_name, String check_sum) {
        this.file_name = file_name;
        this.check_sum = check_sum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getCheck_sum() {
        return check_sum;
    }

    public void setCheck_sum(String check_sum) {
        this.check_sum = check_sum;
    }
}
