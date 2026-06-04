package com.simulator.ocean.config;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BindPlaneProperties.class)
@ConditionalOnProperty(name = "bindplane.enabled", havingValue = "true", matchIfMissing = true)
public class BindPlaneConfig {

    private static final Logger log = LoggerFactory.getLogger(BindPlaneConfig.class);

    private final BindPlaneProperties properties;
    private OpenTelemetrySdk openTelemetry;

    public BindPlaneConfig(BindPlaneProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void install() {
        Resource resource = Resource.getDefault().merge(
                Resource.create(Attributes.of(
                        AttributeKey.stringKey("service.name"), properties.getServiceName()
                ))
        );

        OtlpHttpLogRecordExporter logExporter = OtlpHttpLogRecordExporter.builder()
                .setEndpoint(properties.getHttpEndpoint() + "/v1/logs")
                .build();

        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
                .build();

        openTelemetry = OpenTelemetrySdk.builder()
                .setLoggerProvider(loggerProvider)
                .build();

        OpenTelemetryAppender.install(openTelemetry);
        log.info("BindPlane log bridge installed -> {} (HTTP)", properties.getHttpEndpoint());
    }

    @PreDestroy
    public void shutdown() {
        if (openTelemetry != null) {
            openTelemetry.getSdkLoggerProvider().forceFlush();
            openTelemetry.close();
        }
    }
}
