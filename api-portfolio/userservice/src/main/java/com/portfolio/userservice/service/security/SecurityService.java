package com.portfolio.userservice.service.security;

import com.portfolio.userservice.entity.security.UserAuthDetails;
import com.portfolio.userservice.exception.UserNotFoundException;
import com.portfolio.userservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    public SecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserAuthDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return new UserAuthDetails(user);
    }

}

