package com.portfolio.userservice.entity.security;

import com.portfolio.userservice.entity.User;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class UserAuthDetails implements UserDetails {
    private String username; // Changed from 'name' to 'email' for clarity
    private String password;
    private String email;
    private UUID id;
    private String role;
    private List<GrantedAuthority> authorities;

    public UserAuthDetails(User user) {
        this.username = user.getUsername(); // Use email as username
        this.password = user.getPassword();
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.authorities = Stream.of("user", role)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
