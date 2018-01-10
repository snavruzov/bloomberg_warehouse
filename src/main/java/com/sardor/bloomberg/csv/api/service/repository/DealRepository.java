package com.sardor.bloomberg.csv.api.service.repository;

import com.sardor.bloomberg.csv.api.domain.Deals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by sardor.
 */
@Repository
public interface DealRepository extends JpaRepository<Deals, String> {
}
