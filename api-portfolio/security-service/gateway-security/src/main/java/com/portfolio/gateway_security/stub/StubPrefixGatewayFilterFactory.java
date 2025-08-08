package com.portfolio.gateway_security.stub;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StubPrefixGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> implements Ordered {
  @Override
  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
      log.info("******************************* Hi From Filter *****************************");
      String originalPath = exchange.getRequest().getPath().value();
      log.info("Raw path: {}", originalPath);
      var newPath = originalPath.replaceFirst("api/v1","api/v1/stub");
      exchange =
          exchange
              .mutate()
              .request(
                  builder -> builder
                          .headers(httpHeaders -> httpHeaders.remove("x-stub-enabled"))
                          .path(newPath)
              )
              .build();
      return chain.filter(exchange);
    };
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
