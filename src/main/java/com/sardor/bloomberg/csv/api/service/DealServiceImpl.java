package com.sardor.bloomberg.csv.api.service;

import com.sardor.bloomberg.csv.api.domain.Broken;
import com.sardor.bloomberg.csv.api.domain.Deals;
import com.sardor.bloomberg.csv.api.domain.References;
import com.sardor.bloomberg.csv.api.domain.Statistics;
import com.sardor.bloomberg.csv.api.service.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

/**
 * Created by sardor.
 *
 */
@Service
public class DealServiceImpl extends Persistence implements DealService {
    private final Logger logger = LoggerFactory.getLogger(DealServiceImpl.class);

    private DealRepository dealRepository;
    private BrokenDealRepository brokenDealRepository;
    private OrderingStatRepository orderingStatRepository;
    private ReferencesRepository referencesRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;


    @Autowired
    public DealServiceImpl(DealRepository dealRepository, BrokenDealRepository brokenDealRepository, OrderingStatRepository orderingStatRepository, ReferencesRepository referencesRepository) {
        this.dealRepository = dealRepository;
        this.brokenDealRepository = brokenDealRepository;
        this.orderingStatRepository = orderingStatRepository;
        this.referencesRepository = referencesRepository;
    }

    /**
     * Bulk inserting using JPA entity manager to flush and clear after triggering
     * every certain amount of cycle defined in jdbc.batch_size
     * @param deals collection of deals entity
     */
    @Override
    @Transactional
    public void saveDeal(List<Deals> deals) {
        Assert.notNull(deals, "Deals cannot be null");
        Assert.notEmpty(deals, "Deals cannot be empty");
        try {
            entityManager.flush();
            persistCollection(deals, entityManager);
            entityManager.flush();
            entityManager.clear();
        } catch (Exception e){
            logger.error("Error in saveDeal", e);
        }
    }

    /**
     * Inserting CSV parsed data into the deals table using MySQL LOAD DATA native capabilities
     * @param path correctly parsed csv file path
     * @return number of updated rows
     */
    @Override
    @Transactional
    public int saveDealNative(String path) {
        Assert.notNull(path, "Deals cannot be null");
        int inserted = 0;
        try {
            Query qr = entityManager
                    .createNativeQuery("LOAD DATA LOCAL INFILE '"+path+"' " +
                            "INTO TABLE deals " +
                            "FIELDS TERMINATED BY ',' " +
                            "LINES TERMINATED BY '\\n' " +
                            "(deal_id,from_currency_iso,to_currency_iso,@deal_timestamp,ordering_amount,ref_id)\n" +
                            "SET deal_timestamp = STR_TO_DATE(@deal_timestamp,'%Y-%m-%dT%H:%i:%s')");

            inserted = qr.executeUpdate();

            entityManager.flush();
            entityManager.clear();
        } catch (Exception e){
            logger.error("Error in saveDealNative", e);
        }

        return inserted;
    }

    /**
     * Inserting CSV parsed invalid data into the broken table using MySQL LOAD DATA native capabilities
     * @param path correctly parsed csv file path
     * @return number of updated rows
     */
    @Override
    @Transactional
    public int saveBrokenNative(String path) {
        Assert.notNull(path, "Broken cannot be null");
        int inserted = 0;
        try {
            Query qr = entityManager
                    .createNativeQuery("LOAD DATA LOCAL INFILE '"+path+"' " +
                            "INTO TABLE broken " +
                            "FIELDS TERMINATED BY ',' " +
                            "ENCLOSED BY '\"' "+
                            "LINES TERMINATED BY '\\n' " +
                            "(line_text,ref_id,rec_line)");

            inserted = qr.executeUpdate();

            entityManager.flush();
            entityManager.clear();
        } catch (Exception e){
            logger.error("Error in saveBrokenDealNative", e);
        }

        return inserted;
    }

    /**
     * Bulk inserting using JPA entity manager to flush and clear after triggering
     * every certain amount of cycle defined in jdbc.batch_size
     * @param brokenDeals collection of Broken entities
     */
    @Override
    @Transactional
    public void saveBrokenDeal(List<Broken> brokenDeals) {
        Assert.notNull(brokenDeals, "BrokenDeals is NULL");
        Assert.notEmpty(brokenDeals, "BrokenDeals is EMPTY");
        entityManager.flush();
        persistCollection(brokenDeals,entityManager);
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Saving Statistics data using standard JPA repository implementation
     * @param orderingStat {@link Statistics} object
     */
    @Override
    @Transactional
    public void saveOrderingStat(Statistics orderingStat) {
        Assert.notNull(orderingStat, "OrderingStat is NULL");
        Optional<Statistics> stat = orderingStatRepository.findById(orderingStat.getIsoCode());
        stat.ifPresent(statistics ->
                orderingStat.setCount(orderingStat.getCount() + statistics.getCount()));
        orderingStatRepository.save(orderingStat);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Deals> getDeals(Pageable pageable) {
        return this.dealRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Broken> getBrokenDeals(Pageable pageable) {
        return this.brokenDealRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Statistics> getOrderingStat() {
        return this.orderingStatRepository.findAll();
    }

    /**
     * Saving file {@link References} data, file information,
     * using standard JPA repository implementation
     * @param references {@link References} entity object
     */
    @Override
    @Transactional
    public References saveReferences(References references) {
        Assert.notNull(references, "File references is NULL");
        return this.referencesRepository.save(references);
    }
}
