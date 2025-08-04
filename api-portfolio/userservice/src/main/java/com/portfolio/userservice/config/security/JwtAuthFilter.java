package com.portfolio.userservice.config.security;

import com.portfolio.userservice.entity.security.ContextDomain;
import com.portfolio.userservice.entity.security.PortfolioLink;
import com.portfolio.userservice.exception.UnauthorizedAccessException;
import com.portfolio.userservice.service.security.SecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

  private final SecurityService userDetailsService;
  private final JwtUtil jwtUtil;

  @Autowired
  public JwtAuthFilter(SecurityService userDetailsService, JwtUtil jwtUtil) {
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    log.info("AUTH Started for URI: {}", request.getRequestURI());

    UserDetails userDetails = validateRequest(request);

    if (userDetails != null) {
      var authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    filterChain.doFilter(request, response);
    log.info("AUTH completed");
  }

  private UserDetails validateRequest(HttpServletRequest request) {
    var uri = request.getRequestURI();
    var portfolioLink = PortfolioLink.ofUri(uri);

    // Public endpoints under /auth or requests without ID do not need authentication
    if (ContextDomain.AUTH.equals(portfolioLink.getContextDomain())
        || portfolioLink.getId() == null) {
      return null;
    }

    var token =
        extractTokenFromCookies(request)
            .orElseThrow(() -> new UnauthorizedAccessException("Token is required"));

    var username = jwtUtil.extractUsername(token);
    if (username == null || username.isEmpty()) {
      throw new UnauthorizedAccessException("Token doesn't contain valid username");
    }

    var userDetails = userDetailsService.loadUserByUsername(username);
    if (!jwtUtil.validateToken(token, userDetails)) {
      throw new UnauthorizedAccessException("Invalid or expired token");
    }

    if (!portfolioLink.getId().equals((userDetails).getId())) {
      throw new UnauthorizedAccessException("Token does not match requested user ID");
    }

    return userDetails;
  }

  private Optional<String> extractTokenFromCookies(HttpServletRequest request) {
    return Optional.ofNullable(request.getCookies())
        .flatMap(
            cookies ->
                Arrays.stream(cookies)
                    .filter(cookie -> "token".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst());
  }
}
