package com.portfolio.gateway_security.exception;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserNotFoundException extends RuntimeException {
    List<String> messages;
    public UserNotFoundException(String id) {
        super("User not found with identity: " + id);
    }
}
