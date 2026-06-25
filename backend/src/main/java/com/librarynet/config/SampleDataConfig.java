package com.librarynet.config;

import com.librarynet.service.SampleDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDataConfig {
    @Bean
    ApplicationRunner seedLibraryData(SampleDataService data,
                                      @Value("${librarynet.seed-sample-data:true}") boolean enabled) {
        return args -> {
            if (enabled) data.seedIfEmpty();
        };
    }
}
