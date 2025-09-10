package com.dailyquest.dailyquest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "corsrecord")
public record CorsPropsRecord(   List<String> allowedOrigins,
                                 List<String> allowedMethods,
                                 List<String> allowedHeaders,
                                 boolean allowCredentials
)
{
    public List<String> allowedOrigins() {
        return allowedOrigins != null ? allowedOrigins : List.of("*");
    }
    public List<String> allowedMethods() {
        return allowedMethods != null ? allowedMethods : List.of("GET", "POST", "PUT", "DELETE");
    }
    public List<String> allowedHeaders() {
        return allowedHeaders != null ? allowedHeaders : List.of("*");
    }
}
