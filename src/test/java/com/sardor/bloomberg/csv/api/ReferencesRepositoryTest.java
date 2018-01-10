package com.sardor.bloomberg.csv.api;

import com.sardor.bloomberg.csv.api.domain.References;
import com.sardor.bloomberg.csv.api.service.repository.ReferencesRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sardor.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReferencesRepositoryTest {

    @Autowired
    ReferencesRepository repository;

    @Test
    public void checksAnyOfFileReferences() {
        Iterable<References> ref = this.repository.findAll();
        assertThat(ref.iterator().hasNext()).isFalse(); //Table is empty for the first run
    }
}
