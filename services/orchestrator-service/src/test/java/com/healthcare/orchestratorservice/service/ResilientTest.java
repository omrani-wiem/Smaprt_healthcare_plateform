package com.healthcare.orchestratorservice.service;

import com.healthcare.orchestratorservice.config.ResilienceConfig;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ResilientTest {

    private Resilient newResilient() {
        return new Resilient(
                CircuitBreakerRegistry.of(ResilienceConfig.circuitBreakerConfig()),
                RetryRegistry.of(ResilienceConfig.retryConfig()));
    }

    @Test
    void retrySucceedsAfterTransientFailures() {
        AtomicInteger attempts = new AtomicInteger();
        String result = newResilient().call("svc", () -> {
            if (attempts.incrementAndGet() < 3) throw new IllegalStateException("panne passagère");
            return "ok";
        });
        assertEquals("ok", result);
        assertEquals(3, attempts.get());
    }

    @Test
    void circuitOpensAfterRepeatedFailures() {
        Resilient resilient = newResilient();
        for (int i = 0; i < 3; i++) {
            assertThrows(IllegalStateException.class,
                    () -> resilient.callOnce("svc", () -> { throw new IllegalStateException("down"); }));
        }
        assertThrows(CallNotPermittedException.class,
                () -> resilient.callOnce("svc", () -> "jamais appelé"));
    }
}