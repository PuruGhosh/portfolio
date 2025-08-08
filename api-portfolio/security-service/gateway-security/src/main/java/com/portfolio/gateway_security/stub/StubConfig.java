package com.portfolio.gateway_security.stub;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "stub")
@Data
public class StubConfig {
    @NotNull private List<String> predicates;

    @NotNull
    @NestedConfigurationProperty
    private List<ServiceConfig> services;
}
