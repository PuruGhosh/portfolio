package com.portfolio.userservice.entity.security;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class PortfolioLink {
    private String contextPathService;
    private String contextPathVersion;
    private ContextDomain contextDomain;
    private UUID id;
    private String remainingUri;

    public static PortfolioLink ofUri(String uri) {
        List<String> segments = Arrays.stream(uri.split("/"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (segments.size() < 5 || !"portfolio".equals(segments.get(0))) {
            throw new IllegalArgumentException("Invalid portfolio URI: " + uri);
        }

        String service = segments.get(1);
        String version = segments.get(2) + "/" + segments.get(3);
        String domainStr = segments.get(4);

        ContextDomain domain = ContextDomain.from(domainStr);

        UUID id = null;
        String remaining = null;

        if (segments.size() > 5) {
            String possibleUUID = segments.get(5);
            try {
                id = UUID.fromString(possibleUUID);
                if (segments.size() > 6) {
                    remaining = String.join("/", segments.subList(6, segments.size()));
                }
            } catch (IllegalArgumentException e) {
                remaining = String.join("/", segments.subList(5, segments.size()));
            }
        }

        return new PortfolioLink(service, version, domain, id, remaining);
    }
}
