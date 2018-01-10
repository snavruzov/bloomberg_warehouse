package com.sardor.bloomberg.csv.api.service;

import com.sardor.bloomberg.csv.api.domain.Broken;
import com.sardor.bloomberg.csv.api.domain.Deals;
import com.sardor.bloomberg.csv.api.domain.References;
import com.sardor.bloomberg.csv.api.domain.Statistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by sardor.
 */
public interface DealService {
    Page<Deals> getDeals(Pageable pageable);
    Page<Broken> getBrokenDeals(Pageable pageable);
    Iterable<Statistics> getOrderingStat();

    void saveDeal(List<Deals> deals);
    int saveDealNative(String path);
    int saveBrokenNative(String path);
    void saveBrokenDeal(List<Broken> brokenDeals);
    void saveOrderingStat(Statistics orderingStat);
    References saveReferences(References references);
}
