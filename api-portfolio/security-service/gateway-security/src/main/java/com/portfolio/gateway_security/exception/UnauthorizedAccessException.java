package com.portfolio.gateway_security.exception;

import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class UnauthorizedAccessException extends RuntimeException {
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
