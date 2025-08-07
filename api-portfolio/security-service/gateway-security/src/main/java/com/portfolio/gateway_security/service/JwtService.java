package com.portfolio.gateway_security.service;

import com.portfolio.gateway_security.exception.UnauthorizedAccessException;
import com.portfolio.gateway_security.exception.UserNotFoundException;
import com.portfolio.gateway_security.representation.dto.GetUserDto;
import com.portfolio.gateway_security.representation.dto.UserAuthDto;
import com.portfolio.gateway_security.representation.dto.UserDto;
import com.portfolio.gateway_security.representation.entity.UserAuthEntity;
import com.portfolio.gateway_security.service.ServiceClient.UserClientService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtService {
  @Autowired UserAuthService userService;
  @Autowired UserClientService userClient;

  @Value("${auth.jwt.encoded-secret}")
  private String encodedSecret;

  @Value("${auth.jwt.expiry-hour}")
  private Long expiry;

  private SecretKey secret;

  @PostConstruct
  public void init() {
    var secret = Base64.getDecoder().decode(encodedSecret);
    this.secret = Keys.hmacShaKeyFor(secret);
  }

  private Mono<UserAuthEntity> validateUser(UserAuthDto userDto, Boolean checkPassword) {
    Mono<UserAuthEntity> userAuthEntity;
    if (StringUtils.hasText(userDto.getUsername())) {
      userAuthEntity = userService.getByUsername(userDto.getUsername());
    } else if (!Objects.isNull(userDto.getUserid())) {
      userAuthEntity = userService.getByUserId(userDto.getUserid());
    } else {
      return Mono.error(new IllegalArgumentException("Username or UserId must be provided"));
    }

    return userAuthEntity
        .switchIfEmpty(Mono.error(new UserNotFoundException(userDto.toString())))
        .flatMap(
            user -> {
              log.info("{}", user);
              var encoder = new BCryptPasswordEncoder();
              //                log.info("{}",pwd);
              return checkPassword
                  ? (!user.getPassword().isEmpty()
                          && encoder.matches(userDto.getPassword(), user.getPassword()))
                      ? Mono.just(user)
                      : Mono.error(new UnauthorizedAccessException("Invalid user"))
                  : Mono.just(user);
            });
  }

  public Mono<String> validateUserWithoutTokenAndCreateToken(
      UserAuthDto userDto, Boolean checkPassword) {

    return validateUser(userDto, checkPassword)
        .map(
            user -> {
              Map<String, String> claims = new HashMap<>();
              claims.put("roles", String.join(",", user.getRoles()));
              claims.put("userId", user.getUserId().toString());
              claims.put("username", user.getUsername());
              claims.put("createdAt", user.getCreatedAt().toString());

              return createToken(claims, user.getUserId().toString());
            });
  }

  private String createToken(Map<String, String> claims, String userId) {
    return Jwts.builder()
        .claims(claims)
        .subject(userId)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 1000 * expiry * 60 * 30))
        .signWith(secret)
        .compact();
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public Claims getAllClaims(String token) {
    return Jwts.parser().verifyWith(secret).build().parseSignedClaims(token).getPayload();
  }

  public String extractUsername(String token) {
    return getAllClaims(token).get("username", String.class);
  }

  public String extractUserId(String token) {
    return getAllClaims(token).get("userId", String.class);
  }

  private Boolean checkRolePresent(String token, String role) {
    var roleString = getAllClaims(token).get("roles", String.class);
    log.info("ROLES {}", roleString);
    return StringUtils.hasText(roleString)
        ? roleString.toLowerCase().contains(role.toLowerCase())
        : Boolean.FALSE;
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Mono<Boolean> validateToken(String token, UserAuthDto user) {
    return validateUser(user, false)
        .map(
            userDetails ->
                userDetails.getUsername().equals(extractUsername(token))
                    && userDetails.getUserId().toString().equals(extractUserId(token))
                    && !isTokenExpired(token));
  }

  public Boolean validateTokenForRole(String token, String role) {
    return (checkRolePresent(token, role) && !isTokenExpired(token));
  }

  public Mono<Claims> validateUserAndGetClaim(String token) {
    if (isTokenExpired(token)) {
      return Mono.error(new UnauthorizedAccessException("Token inspired"));
    }
    var claims = getAllClaims(token);
    String userId = claims.get("userId").toString();
    String username = claims.get("username").toString();
    String roles = claims.get("roles").toString();

    return userService
        .getByUserId(UUID.fromString(userId))
        .flatMap(
            userAuthEntity ->
                username.equals(userAuthEntity.getUsername())
                        && (roles.toLowerCase().contains("user")
                            || roles.toLowerCase().contains("admin"))
                    ? Mono.just(claims)
                    : Mono.error(new UnauthorizedAccessException("User Authorized")))
        .switchIfEmpty(Mono.error(new UserNotFoundException(userId)));
  }

  @Transactional
  public Mono<UserDto> register(GetUserDto getUserDto) {
    if (Objects.isNull(getUserDto)) {
      return Mono.error(new IllegalArgumentException("Invalid request"));
    }

    return userService
        .userExists(getUserDto.getUsername())
        .flatMap(
            exists -> {
              if (exists) {
                return Mono.error(new UnauthorizedAccessException("User exists with username"));
              }

              UserDto userRequest = getUserDto.toUserDto();

              return userClient
                  .postUser(userRequest)
                  .flatMap(
                      responseUser -> {
                        List<String> roles = new ArrayList<>(List.of("user"));
                        if (getUserDto.isAdmin()) {
                          roles.add("admin");
                        }

                        UserAuthEntity auth =
                            new UserAuthEntity(
                                responseUser.getId(),
                                getUserDto.getUsername(),
                                getUserDto.getPassword(),
                                roles);

                        return userService
                            .register(auth)
                            .thenReturn(responseUser); // ensures reactive chaining
                      });
            });
  }
}
