package com.sardor.bloomberg.csv.api.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by sardor.
 * Entity class bound to DB table deals that stores valid deals
 */
@Entity
@Table(name = "deals")
public class Deals implements Serializable {
    private static final long serialVersionUID = 7779576516805582466L;

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "deal_id")
    private String deal_id;

    @Column(name = "from_currency_iso")
    private String fromCurrencyIso;

    @Column(name = "to_currency_iso")
    private String toCurrencyIso;

    @Column(name = "deal_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dealTimestamp;

    @Column(name = "ordering_amount")
    private Double orderingAmount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ref_id", referencedColumnName = "ref_id", nullable = false)
    private References ref;

    public Deals() {
    }

    public Deals(String deal_id, String fromCurrencyIso, String toCurrencyIso, Date dealTimestamp, Double orderingAmount, References ref) {
        this.deal_id = deal_id;
        this.fromCurrencyIso = fromCurrencyIso;
        this.toCurrencyIso = toCurrencyIso;
        this.dealTimestamp = dealTimestamp;
        this.orderingAmount = orderingAmount;
        this.ref = ref;
    }

    public Deals(DealsHelper helper, References references) {
        this.deal_id = helper.getUid();
        this.fromCurrencyIso = helper.getFromCurrency();
        this.toCurrencyIso = helper.getToCurrency();
        this.dealTimestamp = helper.getTimestamp();
        this.orderingAmount = helper.getAmount();
        this.ref = references;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getFromCurrencyIso() {
        return fromCurrencyIso;
    }

    public void setFromCurrencyIso(String fromCurrencyIso) {
        this.fromCurrencyIso = fromCurrencyIso;
    }

    public String getToCurrencyIso() {
        return toCurrencyIso;
    }

    public void setToCurrencyIso(String toCurrencyIso) {
        this.toCurrencyIso = toCurrencyIso;
    }

    public Date getDealTimestamp() {
        return dealTimestamp;
    }

    public void setDealTimestamp(Date dealTimestamp) {
        this.dealTimestamp = dealTimestamp;
    }

    public Double getOrderingAmount() {
        return orderingAmount;
    }

    public void setOrderingAmount(Double orderingAmount) {
        this.orderingAmount = orderingAmount;
    }

    public References getRef() {
        return ref;
    }

    public void setRef(References ref) {
        this.ref = ref;
    }
}
