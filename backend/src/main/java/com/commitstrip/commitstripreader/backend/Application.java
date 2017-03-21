package com.commitstrip.commitstripreader.backend;

import com.commitstrip.commitstripreader.backend.config.Configuration;
import com.commitstrip.commitstripreader.backend.service.StripService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
@EnableAutoConfiguration
@EnableSpringDataWebSupport
@EnableCaching
@EnableScheduling
public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, ParseException {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner fetchAllStripFromCommitStripAndStoreThem (StripService service) {
        return (args) -> {

            log.info("Start saving data … ");

            service.fetchAllStripFromCommitStripAndStoreThem();

            log.info(" Ok");

            Configuration.shouldStartToFetchNewStrip = true;

        };
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }

}
