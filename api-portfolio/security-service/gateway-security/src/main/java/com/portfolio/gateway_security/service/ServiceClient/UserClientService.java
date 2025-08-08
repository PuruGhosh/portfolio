package com.portfolio.gateway_security.service.ServiceClient;

import com.portfolio.gateway_security.config.webclient.WebClientConfig;
import com.portfolio.gateway_security.representation.dto.ResponseDto;
import com.portfolio.gateway_security.representation.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class UserClientService {

  @Autowired private final WebClientConfig webClientConfig;
  @Autowired private final DiscoveryClient discoveryClient;

//  private Mono<WebClient> userClient = webClientConfig.getWebClient(ServiceId.USER_SERVICE);

  public Mono<UserDto> postUser(UserDto request) {
      String service = discoveryClient.getServices().stream()
              .filter(s -> s.toLowerCase().contains("user"))
              .findFirst()
              .orElseThrow(() -> new RuntimeException("No user service found"));
      log.info("Service found {}", service);
      return webClientConfig
        .getWebClient(service)
        .flatMap(
            client ->
                client
                    .post()
                    .uri("/api/v1/users/create") // or the correct endpoint
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ResponseDto<UserDto>>() {})
                    .flatMap(
                        responseDto -> {
                            log.info("Response *************/n{}",responseDto);
                          if (responseDto.isError()) {
                            return Mono.error(new RuntimeException("User creation faced error"));
                          }
                          return Mono.just(responseDto.getData());
                        }));
  }
}
