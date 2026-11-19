package com.healthcare.orchestratorservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

    @Bean
    @LoadBalanced
    @Scope("prototype")
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }
}