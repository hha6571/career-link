package com.career.careerlink.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Configuration
public class S3Config {

    @Bean
    public Region awsRegion() {
        return Region.AP_NORTHEAST_2;
    }
}