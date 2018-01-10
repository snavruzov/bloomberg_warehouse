package com.sardor.bloomberg.csv.api.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by sardor.
 * Entity class bound to DB table broken that stores invalid deals
 */

@Entity
@Table(name = "broken")
public class Broken implements Serializable {

    private static final long serialVersionUID = 7056248680319497059L;

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 300)
    private String line_text;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ref_id", referencedColumnName = "ref_id", nullable = false)
    private References ref;

    @Column
    private Long rec_line;

    public Broken() {
    }

    public Broken(String line_text, References ref, Long rec_line) {
        this.line_text = line_text;
        this.ref = ref;
        this.rec_line = rec_line;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLine_text() {
        return line_text;
    }

    public void setLine_text(String line_text) {
        this.line_text = line_text;
    }

    public References getRef() {
        return ref;
    }

    public void setRef(References ref) {
        this.ref = ref;
    }

    public Long getRec_line() {
        return rec_line;
    }

    public void setRec_line(Long rec_line) {
        this.rec_line = rec_line;
    }
}
