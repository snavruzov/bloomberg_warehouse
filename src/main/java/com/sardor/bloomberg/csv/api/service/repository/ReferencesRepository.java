package com.sardor.bloomberg.csv.api.service.repository;

import com.sardor.bloomberg.csv.api.domain.References;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sardor.
 */
@Repository
public interface ReferencesRepository extends CrudRepository<References, Long> {
}
