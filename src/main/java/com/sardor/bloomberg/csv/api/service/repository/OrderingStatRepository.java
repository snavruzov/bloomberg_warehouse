package com.sardor.bloomberg.csv.api.service.repository;

import com.sardor.bloomberg.csv.api.domain.Statistics;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sardor.
 */
@Repository
public interface OrderingStatRepository extends CrudRepository<Statistics, String> {
}
