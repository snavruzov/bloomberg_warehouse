package com.sardor.bloomberg.csv.api.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by sardor on 1/8/18.
 */
@Configuration
public class WarehouseAsyncExecuters {
    /**
     * Dedicated Thread Modeling to handle POST Requests for uploaded files
     * process
     */
    @Bean
    public ExecutorService csvPostExecutorService() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("csvPostExecutor-%d")
                .setDaemon(true)
                .build();
        ExecutorService es = Executors.newCachedThreadPool(threadFactory);
        return es;
    }
}
