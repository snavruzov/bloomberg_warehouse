package com.sardor.bloomberg.csv.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sardor.bloomberg.csv.api.domain.CustomResponse;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

/**
 * Created by sardor.
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class MVCTests {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void testHome() throws Exception {
        this.mvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string("Bloomberg Warehouse"));
    }

    @Test
    public void testDeals() throws Exception {
        MvcResult res = this.mvc.perform(get("/deals", "page=1"))
                .andExpect(status().isOk()).andReturn();
        System.out.println(res.getResponse().getContentAsString());

    }

    @Test
    public void testBroken() throws Exception {
        MvcResult res = this.mvc.perform(get("/broken", "page=1"))
                .andExpect(status().isOk()).andReturn();
        System.out.println(res.getResponse().getContentAsString());
    }

    @Test
    public void testStat() throws Exception {
        MvcResult res = this.mvc.perform(get("/stat"))
                .andExpect(status().isOk()).andReturn();
        System.out.println(res.getResponse().getContentAsString());
    }

    @Test
    public void shouldUploadedFile() throws Exception {
        String csv = "hash,pickone,pickone,date,natural\n" +
                "c9f40757dcfa783a35174ea321cb6a973c0f9549,UBA,,2017-10-12T23:45:30.384+0400,34343\n" +
                "07c8f15069a038e1d69fa99544eae9b04126a3a4,EUR,EUR,2017-10-12T23:45:30.384+0400,8813";

        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "csv1mock.csv",
                "text/csv", csv.getBytes());
        this.mvc.perform(multipart("/api/upload").file(multipartFile))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUploadedBigFile() throws Exception {
        String csvPath = "src/test/java/resources/convertcsv.csv";

        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "convertcsv.csv",
                        "text/csv", Files.readAllBytes(Paths.get(csvPath)));
        MvcResult resultMvc = this.mvc.perform(multipart("/api/upload").file(multipartFile))
                .andExpect(status().isOk()).andReturn();
        System.out.println(resultMvc.getAsyncResult());
    }

    @Test
    public void shouldReturnCustomResponseIfFileExistSame() throws Exception {
        String csv = "hash,pickone,pickone,date,natural\n" +
                "c9f40757dcfa783a35174ea321cb6a973c0f9549,UBA,,2017-10-12T23:45:30.384+0400,34343\n" +
                "07c8f15069a038e1d69fa99544eae9b04126a3a4,EUR,EUR,2017-10-12T23:45:30.384+0400,8813";

        MockMultipartFile multipartFile =
                new MockMultipartFile("file", "convertcsv.csv",
                        "text/csv", csv.getBytes());
        MvcResult resultMvc = this.mvc.perform(multipart("/api/upload").file(multipartFile))
                .andExpect(status().isOk()).andReturn();
        resultMvc.getRequest().getAsyncContext().setTimeout(5000);

        resultMvc = this.mvc
                .perform(asyncDispatch(resultMvc))
                .andExpect(status().isOk()).andReturn();

        boolean reslt = resultMvc.getResponse().getContentAsString().contains("0004");
        Assertions.assertThat(reslt).isTrue();
    }
}
