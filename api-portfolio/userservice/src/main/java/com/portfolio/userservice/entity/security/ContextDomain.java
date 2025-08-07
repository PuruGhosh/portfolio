package com.portfolio.userservice.entity.security;


import java.util.Arrays;
import java.util.Optional;

public enum ContextDomain {
    UNKNOWN(""),
    USERS("users"),
    PROJECTS("projects"),
    SKILLS("skills");

    private final String value;

    ContextDomain(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ContextDomain from(String input) {
        return Arrays.stream(ContextDomain.values())
                .filter(cd -> cd != UNKNOWN && cd.value.equalsIgnoreCase(input))
                .findFirst()
                .orElse(UNKNOWN);
    }
}

