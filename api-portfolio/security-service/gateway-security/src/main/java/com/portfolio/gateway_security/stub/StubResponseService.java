package com.portfolio.gateway_security.stub;

import com.portfolio.gateway_security.representation.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StubResponseService {

  private final StubConfig stubConfig;

  public Mono<ResponseEntity<ResponseDto<?>>> getStubResponse(String service, String path, String httpMethod) {
    if (service == null || service.isBlank()) {
      throw new IllegalArgumentException("Expecting service name");
    }

//    log.info("Stub service: {}", service);
    path = normalizePath(path);
//    log.info("Stub path: {}", path);

    return findMatchingStub(service, path, httpMethod)
            .map(this::generateStubResponse)
            .orElseGet(() -> Mono.just(ResponseEntity.notFound().build()));
  }

  private String normalizePath(String path) {
    if (path == null || path.isBlank() || isUUID(path)) {
      return "/";
    }
    return path.startsWith("/") ? path : "/" + path;
  }

  private boolean isUUID(String s) {
    try {
      UUID.fromString(s);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Optional<RequestConfig> findMatchingStub(String service, String path, String httpMethod) {
    return stubConfig.getServices().stream()
            .filter(s -> s.getName().equalsIgnoreCase(service))
            .flatMap(s -> s.getRequests().stream())
            .filter(r -> r.getPath().equalsIgnoreCase(path)
                    && r.getMethods().contains(httpMethod.toUpperCase()))
            .findFirst();
  }

  private Mono<ResponseEntity<ResponseDto<?>>> generateStubResponse(RequestConfig config) {
    try {
      Class<?> clazz = Class.forName(config.getResponseClass());
      int count = parseResponseCount(config.getReponseType());

      List<Object> sampleDataList = invokeSampleData(clazz, count);
      Object response = config.getReponseType().startsWith("list")
              ? sampleDataList
              : sampleDataList.get(0);

      return Mono.just(ResponseEntity.ok(ResponseDto.success(response)));
    } catch (ClassNotFoundException e) {
      log.error("Response class not found: {}", config.getResponseClass(), e);
      return error("Response class not found: " + config.getResponseClass());
    } catch (Exception e) {
      log.error("Error generating stub response", e);
      return error("Stub generation failed");
    }
  }

  private int parseResponseCount(String responseType) {
    if (!responseType.startsWith("list")) return 1;
    try {
      return Integer.parseInt(responseType.replaceFirst("^list-", ""));
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  private List<Object> invokeSampleData(Class<?> clazz, int count) throws Exception {
//    log.info("count-------->{}", count);
    Method method = clazz.getMethod("getSampleData");

    if (!Modifier.isStatic(method.getModifiers())) {
      throw new IllegalStateException("getSampleData() must be static");
    }

    List<Object> list = new ArrayList<>();
    for (int i = 0; i < Math.max(1, count); i++) {
//      log.info("i---->{}",i);
      list.add(method.invoke(null));
    }
    return list;
  }

  private Mono<ResponseEntity<ResponseDto<?>>> error(String message) {
    return Mono.just(ResponseEntity.internalServerError()
            .body(ResponseDto.failure(List.of(message))));
  }
}
