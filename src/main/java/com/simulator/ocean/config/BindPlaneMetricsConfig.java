package com.simulator.ocean.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.registry.otlp.OtlpConfig;
import io.micrometer.registry.otlp.OtlpMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "bindplane.enabled", havingValue = "true", matchIfMissing = true)
public class BindPlaneMetricsConfig {

    private static final Logger log = LoggerFactory.getLogger(BindPlaneMetricsConfig.class);

    @Bean(destroyMethod = "close")
    public OtlpMeterRegistry otlpMeterRegistry(BindPlaneProperties properties) {
        String url = properties.getHttpEndpoint() + "/v1/metrics";
        log.info("BindPlane metrics export configured -> {} (HTTP)", url);

        OtlpConfig config = new OtlpConfig() {
            @Override public String url()  { return url; }
            @Override public Duration step() { return Duration.ofSeconds(3); }
            @Override public Map<String, String> resourceAttributes() {
                return Map.of("service.name", properties.getServiceName());
            }
            @Override public String get(String key) { return null; }
        };

        return new OtlpMeterRegistry(config, Clock.SYSTEM);
    }
}
