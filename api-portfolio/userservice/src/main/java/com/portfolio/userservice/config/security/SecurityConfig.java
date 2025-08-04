package com.portfolio.userservice.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;

  @Value("${server.servlet.context-path}")
  private String pathContext;


  // Constructor injection for required dependencies
  public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.userDetailsService = userDetailsService;
  }

  /*
   * Main security configuration
   * Defines endpoint access rules and JWT filter setup
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // Disable CSRF (not needed for stateless JWT)
        .csrf(AbstractHttpConfigurer::disable)

        // Configure endpoint authorization
        .authorizeHttpRequests(
            auth -> auth.requestMatchers("/auth/**").permitAll()

                    // Permit everything for ADMIN role
                    .requestMatchers("/getAll/**").hasRole("admin")
                    .requestMatchers("/create/**").hasRole("admin")
                    .anyRequest()

                    // For everyone else: require authentication
                    .authenticated())

        // Stateless session (required for JWT)
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(
                        (request, response, authException) -> {
                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType("application/json");
                          response
                              .getWriter()
                              .write(
                                  "{\"error\": \"Unauthorized: "
                                      + authException.getMessage()
                                      + "\"}");
                        })
                    .accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType("application/json");
                          response
                              .getWriter()
                              .write(
                                  "{\"error\": \"Forbidden: "
                                      + accessDeniedException.getMessage()
                                      + "\"}");
                        }))

        // Set custom authentication provider
        .authenticationProvider(authenticationProvider())

        // Add JWT filter before Spring Security's default filter
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /*
   * Password encoder bean (uses BCrypt hashing)
   * Critical for secure password storage
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /*
   * Authentication provider configuration
   * Links UserDetailsService and PasswordEncoder
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  /*
   * Authentication manager bean
   * Required for programmatic authentication (e.g., in /generateToken)
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
