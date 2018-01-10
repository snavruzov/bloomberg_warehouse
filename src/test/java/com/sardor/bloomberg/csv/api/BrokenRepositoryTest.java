package com.sardor.bloomberg.csv.api;

import com.sardor.bloomberg.csv.api.domain.Broken;
import com.sardor.bloomberg.csv.api.service.repository.BrokenDealRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sardor.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BrokenRepositoryTest {
    @Autowired
    BrokenDealRepository brokenDealRepository;

    @Test
    public void findsFirstPageOfBrokenDeals() {
        Page<Broken> brnDeals = this.brokenDealRepository
                .findAll(PageRequest.of(0, 10));
        assertThat(brnDeals.getTotalElements()).isNotNegative();
    }

}
