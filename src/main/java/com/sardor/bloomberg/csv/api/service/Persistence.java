package com.sardor.bloomberg.csv.api.service;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sardor.
 */

public abstract class Persistence {

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    <T> void persistCollection(Collection<T> list, EntityManager manager){
        final AtomicInteger cnt = new AtomicInteger(0);
        list.forEach(t -> {
            manager.persist(t);
            if (cnt.incrementAndGet() % batchSize == 0) {
                manager.flush();
                manager.clear();
            }
        });
    }
}
