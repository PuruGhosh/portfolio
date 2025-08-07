package com.portfolio.gateway_security.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RestrictedResourceGatewayFilterFactory extends AbstractGatewayFilterFactory<Object>
    implements Ordered {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
      Map<String, String> pathVariables =
          exchange.getAttribute(ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

      String requestedUuid = pathVariables != null ? pathVariables.get("id") : null;
      log.info("request id in uri {}",requestedUuid);
      if (requestedUuid == null || !isValidUUID(requestedUuid)) {
        return deny(exchange, HttpStatus.BAD_REQUEST, "Invalid UUID");
      }

      String encodedClaim =
          Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("jwt-claim"))
              .orElse(null);

      if (encodedClaim == null) {
        return deny(exchange, HttpStatus.BAD_REQUEST, "Token is missing");
      }

      String userIdFromToken;
      try {
        byte[] decoded = Base64.getDecoder().decode(encodedClaim);
        Map<String, String> claimMap = objectMapper.readValue(decoded, new TypeReference<>() {});
        log.info("claims {}", claimMap);
        userIdFromToken = claimMap.get("userId");
        log.info("Userid from token {}",userIdFromToken);
        if (userIdFromToken == null || !isValidUUID(userIdFromToken)) {
          return deny(exchange, HttpStatus.BAD_REQUEST, "Invalid userId in token");
        }

      } catch (Exception e) {
        log.info("ex:", e);
        return deny(exchange, HttpStatus.BAD_REQUEST, "Malformed token");
      }

      if (!userIdFromToken.equals(requestedUuid)) {
        return deny(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized user");
      }
      log.info("Filter complete ******************");
      return chain.filter(exchange);
    };
  }

  private boolean isValidUUID(String value) {
    try {
      UUID.fromString(value);
      log.info("True uuid*****************************");
      return true;
    } catch (Exception e) {
      log.info("false uuid**************************");
      return false;
    }
  }

  private Mono<Void> deny(ServerWebExchange exchange, HttpStatus status, String message) {
    exchange.getResponse().setStatusCode(status);
    exchange.getResponse().getHeaders().add("Content-Type", "text/plain; charset=UTF-8");
    byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
    return exchange
        .getResponse()
        .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
