package com.portfolio.gateway_security.config.webclient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
@Slf4j
public class WebClientConfig {
  private final RouteLocator routeLocator;


  @Bean
  @LoadBalanced
  public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
  }

  public Mono<WebClient> getWebClient(String serviceId) {
    return routeLocator
        .getRoutes()
        .filter(route -> route.getId().equalsIgnoreCase(serviceId))
        .next()
        .map(route -> loadBalancedWebClientBuilder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl(route.getUri().toString().replace("lb","http")).build());
  }
}
