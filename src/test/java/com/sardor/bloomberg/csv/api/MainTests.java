package com.sardor.bloomberg.csv.api;

import com.sardor.bloomberg.csv.api.domain.CsvResult;
import com.sardor.bloomberg.csv.api.domain.References;
import com.sardor.bloomberg.csv.api.service.CsvService;
import com.sardor.bloomberg.csv.api.service.DealService;
import com.sardor.bloomberg.csv.api.service.csv.*;
import org.apache.commons.collections4.ListUtils;
import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MainTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void csvBuilderUnsupportedOperationExceptionTest() {
		final SimpleDateFormat formatter = new SimpleDateFormat(CsvUtils.DATE_FORMAT);

		thrown.expect(UnsupportedOperationException.class);
		new CsvParserBuilder().
				separator(',').
				quoteChar(',').
				trimWhitespace(true).
				allowUnixTime(false).
				formatter(formatter).
				build();
	}

	@Test
	public void csvParserTest() throws IOException {
		final SimpleDateFormat formatter = new SimpleDateFormat(CsvUtils.DATE_FORMAT);


		CsvResult result = new CsvResult();
		FileReader fr = new FileReader("src/test/java/resources/convertcsv.csv");
		StopWatch watch = new StopWatch("Test Parser benchmark: csvParserTest");
		watch.start();
		CsvParser p = new CsvParserBuilder().
				separator(',').
				quoteChar('"').
				trimWhitespace(true).
				allowUnixTime(false).
				formatter(formatter).
				build();
		CsvReader csvr = new CsvReaderBuilder(fr).
				csvParser(p).
				skipLines(1).
				build();

		while (csvr.iterator().hasNext()) {
			if (csvr.deals.isError()) {
				result.invalidDeals++;
			} else {
				result.validDeals++;
				long cnt = result.orderingDealsCount.getOrDefault(
						csvr.deals.getFromCurrency(), 0L);
				result.orderingDealsCount.put(
						csvr.deals.getFromCurrency(), cnt + 1);
			}
		}

		watch.stop();
		assertThat(watch.getTotalTimeMillis()).isLessThan(5000);

		csvr.close();
	}

}
