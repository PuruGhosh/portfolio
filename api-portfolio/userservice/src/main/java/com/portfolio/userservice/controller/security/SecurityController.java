package com.portfolio.userservice.controller.security;

import com.portfolio.userservice.config.security.JwtUtil;
import com.portfolio.userservice.entity.dto.GetUserDto;
import com.portfolio.userservice.entity.dto.ResponseDto;
import com.portfolio.userservice.entity.dto.UserDto;
import com.portfolio.userservice.entity.security.UserAuthRequest;
import com.portfolio.userservice.service.UserService;
import com.portfolio.userservice.service.security.SecurityService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class SecurityController {

  private final SecurityService securityService;
  private final UserService userService;

  private final JwtUtil jwtUtil;

  private final AuthenticationManager authenticationManager;

  @GetMapping("/welcome")
  public String welcome() {
    return "Welcome this endpoint is not secure";
  }

  @PostMapping
  public ResponseEntity<ResponseDto<UserDto>> createUser(@RequestBody GetUserDto userDto) {
    log.info("Request received: CREATE user - payload: {}", userDto);
    UserDto createdUser = userService.createUser(userDto);
    log.info("User created with ID: {}", createdUser.getId());
    return ResponseEntity.ok(ResponseDto.success(createdUser));
  }

  // Removed the role checks here as they are already managed in SecurityConfig

  @PostMapping("/generateToken")
  public ResponseEntity<ResponseDto<Map<String, String>>> authenticateAndGetToken(
      @RequestBody UserAuthRequest authRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getUserName(), authRequest.getPassword()));
    if (authentication.isAuthenticated()) {
      String username = authRequest.getUserName();
      return new ResponseEntity<>(
          ResponseDto.success(Map.of(
                  "token",
                  jwtUtil.createToken(getClaims(username),
                          username))),
          HttpStatus.CREATED);
    } else {
      throw new UsernameNotFoundException("Invalid user request!");
    }
  }

  Map<String, String> getClaims(String username) {
    var user = securityService.loadUserByUsername(username);
    Map<String, String> claims = new HashMap<>();
    claims.put("id", user.getId().toString());
    claims.put("username", user.getUsername());
    claims.put("email", user.getEmail());
    claims.put("role", user.getRole());
    return claims;
  }
}
