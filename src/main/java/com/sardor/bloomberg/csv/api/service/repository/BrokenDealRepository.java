package com.sardor.bloomberg.csv.api.service.repository;

import com.sardor.bloomberg.csv.api.domain.Broken;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sardor.
 */
@Repository
public interface BrokenDealRepository extends PagingAndSortingRepository<Broken, Long> {
}
