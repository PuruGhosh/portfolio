package com.portfolio.gateway_security.stub;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RequestConfig {
    @NotNull private String path;
    @NotNull private List<String> methods;
    @NotNull private String responseClass;
    @NotNull private String reponseType;
}
