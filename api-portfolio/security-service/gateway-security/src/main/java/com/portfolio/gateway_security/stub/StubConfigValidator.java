package com.portfolio.gateway_security.stub;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StubConfigValidator {

    private final StubConfig stubConfig;

    @PostConstruct
    public void validate() {
        List<ServiceConfig> services = stubConfig.getServices();
        for (ServiceConfig service : services) {
            for (RequestConfig request : service.getRequests()) {
                String responseClassName = request.getResponseClass();

                try {
                    Class<?> clazz = Class.forName(responseClassName);

                    Method sampleMethod = clazz.getDeclaredMethod("getSampleData");
                    if (!java.lang.reflect.Modifier.isStatic(sampleMethod.getModifiers())) {
                        throw new IllegalStateException("Method getSampleData() in " + responseClassName + " is not static");
                    }

                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Response class not found: " + responseClassName, e);
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException("No public static getSampleData() method in: " + responseClassName, e);
                }
            }
        }

        log.info("StubConfig validation passed.");
    }
}

