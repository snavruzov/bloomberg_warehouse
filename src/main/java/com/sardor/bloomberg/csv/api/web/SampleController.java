package com.sardor.bloomberg.csv.api.web;

import com.sardor.bloomberg.csv.api.domain.*;
import com.sardor.bloomberg.csv.api.service.CsvService;
import com.sardor.bloomberg.csv.api.service.DealService;
import com.sardor.bloomberg.csv.api.service.csv.CsvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by sardor.
 * Controller class, this is a main entry point to perform HTTP requests
 */
@CrossOrigin
@RestController
public class SampleController {
    private final Logger logger = LoggerFactory.getLogger(SampleController.class);

    private static String UPLOADED_FOLDER = "/tmp/";
    ExecutorService executor = Executors.newWorkStealingPool();

    @Autowired
    private DealService service;

    @Autowired
    @Qualifier("csvPostExecutorService")
    private ExecutorService csvPostExecutor;


    @RequestMapping(value = "/", method = RequestMethod.OPTIONS)
    public void entryPoint(){}

    @RequestMapping("/")
    @Transactional(readOnly = true)
    public String index(){
        return "Bloomberg Warehouse";
    }

    @RequestMapping(value="/deals",method= RequestMethod.GET)
    public Page<Deals> listDeal(Pageable pageable){
        Page<Deals> deals = service.getDeals(pageable);
        return deals;
    }

    @RequestMapping(value="/broken",method= RequestMethod.GET)
    public Page<Broken> listBroken(Pageable pageable){
        Page<Broken> broken = service.getBrokenDeals(pageable);
        logger.info("/broken response {}", broken.getTotalElements());
        return broken;
    }

    @RequestMapping(value="/stat",method= RequestMethod.GET)
    public Iterable<Statistics> listStat(){
        List<Statistics> stats = new ArrayList<>();
        Iterable<Statistics> ordering = service.getOrderingStat();
        ordering.iterator().forEachRemaining(stats::add);
        return stats;
    }


    @RequestMapping(value = "/api/upload", method = RequestMethod.POST)
    public DeferredResult<ResponseEntity<?>> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Start file processing! {}", file.getName());
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>();


        if (file.isEmpty()) {
            deferredResult.setResult(new ResponseEntity<>(new CustomResponse("0001",
                    "Please select a CSV file!",null,true), HttpStatus.OK));
            return deferredResult;
        }

        if (file.getSize()>9*1024*1024) {
            deferredResult.setResult(new ResponseEntity<>(new CustomResponse("0002",
                    "Attachment size exceeds the allowable limit! (10MB)!",
                    null,true), HttpStatus.OK));
            return deferredResult;
        }

        try {
            CompletableFuture.supplyAsync(() -> {
                ResponseEntity<?> resp = null;
                try {
                    CsvResult res = saveUploadedFiles(file);
                    resp = new ResponseEntity<>(new CustomResponse("0003",
                            "Successfully processed - " + file.getOriginalFilename()
                            ,res
                            , false),
                            new HttpHeaders(), HttpStatus.OK);
                } catch (Exception e){
                    logger.error("Error while applying async proc", e);
                    resp = new ResponseEntity<>(new CustomResponse("0004",
                            "Something wrong happened !!"+ "["+e.getMessage()+"]"
                            , null
                            ,true), HttpStatus.OK);
                }
                return resp;
            }, csvPostExecutor)
                    .whenComplete((p, throwable) ->
                            {
                                logger.debug("Current Thread Name :{}", Thread.currentThread().getName());
                                deferredResult.setResult(p);
                            }


                    );
        } catch (Exception e) {
            logger.error("Exception while file processing",e);
            deferredResult.setResult(new ResponseEntity<>(new CustomResponse("0004",
                    "Something wrong happened on a back-end side!!"+
                            "["+e.getMessage()+"]", null,true), HttpStatus.OK));
            return deferredResult;
        }

         return deferredResult;

    }

    /**
     * The method to upload and process passed multipart files
     * method saves the file info into the DB and starts working on CSV formatted file
     * @throws Exception throws in case of duplicate file, CSV file reader exception, saving error and
     * internally caused exception
     */
    private CsvResult saveUploadedFiles(MultipartFile file) throws Exception {

        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
        if(path.toFile().exists()) {
            throw new UnsupportedOperationException("File already processed!");
        }

        Files.write(path, bytes);
        References references = new References(file.getOriginalFilename(),"Todo");
        references = this.service.saveReferences(references);
        System.out.println("Test");

        StopWatch watch = new StopWatch();
        watch.start();
        CsvService csvService = new CsvService(path.toString(),
                ',',
                '"',
                false,
                true,
                CsvUtils.DATE_FORMAT,
                this.service);
        CsvResult result = csvService.parseCsv(references);

        if(!this.executor.isShutdown()) {
            this.executor.shutdown();
        }

        result.orderingDealsCount
                .forEach((k,v) -> this.service.saveOrderingStat(new Statistics(k,v)));

        watch.stop();
        result.benchmark = watch.getTotalTimeMillis();
        logger.info(watch.prettyPrint());
        return result;
    }
}
