package com.healthcare.orchestratorservice.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class Resilient {

    private static final Logger log = LoggerFactory.getLogger(Resilient.class);

    private final CircuitBreakerRegistry breakers;
    private final RetryRegistry retries;

    /** Lecture : circuit breaker + retry (le breaker est à l'intérieur : chaque tentative compte). */
    public <T> T call(String name, Supplier<T> action) {
        CircuitBreaker breaker = breakers.circuitBreaker(name);
        Retry retry = retries.retry(name);
        return Retry.decorateSupplier(retry, CircuitBreaker.decorateSupplier(breaker, action)).get();
    }

    /** Lecture avec valeur de repli si le service reste indisponible. */
    public <T> T call(String name, Supplier<T> action, Supplier<T> fallback) {
        try {
            return call(name, action);
        } catch (Exception e) {
            log.warn("Repli pour '{}' : {}", name, e.getMessage());
            return fallback.get();
        }
    }

    /** Écriture non idempotente : circuit breaker seulement, JAMAIS de retry. */
    public <T> T callOnce(String name, Supplier<T> action) {
        return CircuitBreaker.decorateSupplier(breakers.circuitBreaker(name), action).get();
    }
}