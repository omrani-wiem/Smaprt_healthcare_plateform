package com.healthcare.orchestratorservice.config;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.github.resilience4j.micrometer.tagged.TaggedRetryMetrics;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    private static final Logger log = LoggerFactory.getLogger(ResilienceConfig.class);

    public static CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(6)
                .minimumNumberOfCalls(3)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .permittedNumberOfCallsInHalfOpenState(2)
                .ignoreExceptions(HttpClientErrorException.class)
                .build();
    }

    public static RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofMillis(300), 2))
                .retryOnException(e -> !(e instanceof HttpClientErrorException)
                        && !(e instanceof CallNotPermittedException))
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(MeterRegistry meters) {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(circuitBreakerConfig());
        TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(registry).bindTo(meters);
        registry.getEventPublisher().onEntryAdded(e ->
                e.getAddedEntry().getEventPublisher().onStateTransition(t ->
                        log.warn("Circuit breaker '{}' : {}", t.getCircuitBreakerName(), t.getStateTransition())));
        return registry;
    }

    @Bean
    public RetryRegistry retryRegistry(MeterRegistry meters) {
        RetryRegistry registry = RetryRegistry.of(retryConfig());
        TaggedRetryMetrics.ofRetryRegistry(registry).bindTo(meters);
        return registry;
    }
}
