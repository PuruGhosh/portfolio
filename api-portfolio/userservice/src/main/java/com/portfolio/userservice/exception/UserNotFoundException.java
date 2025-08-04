package com.portfolio.userservice.exception;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserNotFoundException extends RuntimeException {
    List<String> messages;
    public UserNotFoundException(String id) {
        super("User not found with id: " + id);
    }
}
