package com.portfolio.gateway_security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.gateway_security.service.JwtService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DefaultDomainFilter implements GlobalFilter, Ordered {

    private final String jwtCookieName;
    private final JwtService jwtService;
    private static final ObjectMapper mapper = new ObjectMapper();

    public DefaultDomainFilter(@Value("${auth.jwt.cookie-name}") String jwtCookieName,
                               JwtService jwtService) {
        this.jwtCookieName = jwtCookieName;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Hello1");
        String jwtToken = Optional.ofNullable(exchange.getRequest().getCookies().getFirst(jwtCookieName))
                .map(HttpCookie::getValue)
                .orElse(null);
        var mutatedExchange = exchange.mutate()
                .request(builder -> builder.header("x-gateway-processed", "y"))
                .build();
        log.info("Hello 2");
        if (jwtToken == null || jwtToken.isBlank()) {
            return chain.filter(exchange); // No token, proceed without claims
        }

        return jwtService.validateUserAndGetClaim(jwtToken)
                .flatMap(claims -> {
                    try {
                        String base64Claims = Base64.getEncoder()
                                .encodeToString(mapper.writeValueAsString(claims).getBytes(StandardCharsets.UTF_8));

                        ServerWebExchange finalExchange = mutatedExchange.mutate()
                                .request(builder -> builder.header("jwt-claim", base64Claims))
                                .build();
                        finalExchange.getResponse().getHeaders().add("jwt-claim", base64Claims);

                        return chain.filter(finalExchange);
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                })
                .onErrorResume(ex -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return mutatedExchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
