package com.sardor.bloomberg.csv.api;

import com.sardor.bloomberg.csv.api.domain.Deals;
import com.sardor.bloomberg.csv.api.service.repository.DealRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by sardor.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class DealsRepositoryTest {

    @Autowired
    DealRepository repository;

    @Test
    public void findsFirstPageOfDeals() {
        Page<Deals> deals = this.repository.findAll(PageRequest.of(0, 10));
        assertThat(deals.getTotalElements()).isNotNegative();
    }
}
