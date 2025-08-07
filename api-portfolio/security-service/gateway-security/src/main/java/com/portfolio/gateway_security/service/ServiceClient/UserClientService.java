package com.portfolio.gateway_security.service.ServiceClient;

import com.portfolio.gateway_security.config.webclient.ServiceId;
import com.portfolio.gateway_security.config.webclient.WebClientConfig;
import com.portfolio.gateway_security.representation.dto.ResponseDto;
import com.portfolio.gateway_security.representation.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class UserClientService {

  @Autowired private final WebClientConfig webClientConfig;

//  private Mono<WebClient> userClient = webClientConfig.getWebClient(ServiceId.USER_SERVICE);

  public Mono<UserDto> postUser(UserDto request) {
    return webClientConfig
        .getWebClient(ServiceId.USER_SERVICE)
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
