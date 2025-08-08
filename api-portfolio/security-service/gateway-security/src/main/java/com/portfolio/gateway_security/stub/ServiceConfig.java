package com.portfolio.gateway_security.stub;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class ServiceConfig {
    @NotNull private String name;
    @NotNull private List<RequestConfig> requests;
}
