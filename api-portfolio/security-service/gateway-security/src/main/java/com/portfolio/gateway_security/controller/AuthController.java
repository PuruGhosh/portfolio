package com.portfolio.gateway_security.controller;

import com.portfolio.gateway_security.exception.UnauthorizedAccessException;
import com.portfolio.gateway_security.representation.dto.GetUserDto;
import com.portfolio.gateway_security.representation.dto.ResponseDto;
import com.portfolio.gateway_security.representation.dto.UserAuthDto;
import com.portfolio.gateway_security.representation.dto.UserDto;
import com.portfolio.gateway_security.service.JwtService;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/auth")
@Slf4j
public class AuthController {

  private final JwtService jwtService;

  @Value("${auth.jwt.cookie-name}")
  private String jwtCookieName;

  public AuthController(JwtService authService) {
    this.jwtService = authService;
  }

  @PostMapping("/login")
  public Mono<ResponseEntity<ResponseDto<String>>> login(@RequestBody UserAuthDto userDto) {
    return jwtService
        .validateUserWithoutTokenAndCreateToken(userDto, true)
        .map(
            token -> {
              ResponseCookie cookie =
                  ResponseCookie.from(jwtCookieName, token)
                      .httpOnly(true)
                      .path("/")
                      .maxAge(60 * 30) // 30 minutes
                      .build();

              return ResponseEntity.ok()
                  .header("Set-Cookie", cookie.toString())
                  .body(ResponseDto.success(token));
            });
  }

  @PostMapping("/logout")
  public Mono<ResponseEntity<ResponseDto<Void>>> logout() {
    // Clear the cookie
    ResponseCookie clearCookie =
        ResponseCookie.from(jwtCookieName, "").httpOnly(true).path("/").maxAge(0).build();

    return Mono.just(
        ResponseEntity.ok()
            .header("Set-Cookie", clearCookie.toString())
            .body(ResponseDto.success(null)));
  }

  @GetMapping("/{userid}/token")
  public Mono<ResponseEntity<ResponseDto<String>>> getToken(
      @PathVariable UUID userid, ServerWebExchange exchange) {
    String jwtToken =
        Optional.ofNullable(exchange.getRequest().getCookies().getFirst(jwtCookieName))
            .map(HttpCookie::getValue)
            .orElse(null);
    log.info("Token= {}", jwtToken);

    if (!jwtService.validateTokenForRole(jwtToken, "admin")) {
      throw new UnauthorizedAccessException("User is not authorized for the operation.");
    }
    UserAuthDto dto = new UserAuthDto();
    dto.setUserid(userid);
    return jwtService
        .validateUserWithoutTokenAndCreateToken(dto, Boolean.FALSE)
        .map(token -> ResponseEntity.ok(ResponseDto.success(token)));
  }

  @PostMapping("/{userid}/token")
  public Mono<ResponseEntity<ResponseDto<String>>> validateToken(
      @PathVariable UUID userid, @RequestBody Map<String, String> map, ServerWebExchange exchange) {
    String jwtToken =
        Optional.ofNullable(exchange.getRequest().getCookies().getFirst(jwtCookieName))
            .map(HttpCookie::getValue)
            .orElse(null);
    if (!jwtService.validateTokenForRole(jwtToken, "admin")) {
      throw new UnauthorizedAccessException("User is not authorized for the operation.");
    }
    UserAuthDto dto = new UserAuthDto();
    dto.setUserid(userid);
    var token = map.get("token");
    if (!StringUtils.hasText(token)) {
      throw new IllegalArgumentException("Add token.");
    }
    return jwtService
        .validateToken(token, dto)
        .map(
            valid -> {
              var msg = valid ? "VALID" : "INVALID";
              return ResponseEntity.ok(ResponseDto.success(msg));
            });
  }

  @PostMapping("/regester")
  public Mono<ResponseEntity<ResponseDto<UserDto>>> regesterUser(@RequestBody GetUserDto getUserDto){
    return jwtService.register(getUserDto).map(user->
            ResponseEntity.ok(ResponseDto.success(user)));
  }
}
