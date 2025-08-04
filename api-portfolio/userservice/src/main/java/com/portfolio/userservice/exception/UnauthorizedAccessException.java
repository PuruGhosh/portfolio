package com.portfolio.userservice.exception;

import java.util.List;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.AuthenticationException;

@EqualsAndHashCode(callSuper = false)
public class UnauthorizedAccessException extends AuthenticationException {
    List<String> messages;

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
