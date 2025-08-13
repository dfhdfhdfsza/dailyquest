package com.dailyquest.dailyquest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="app.cors")
public class CorsProps {
    private java.util.List<String> allowedOrigins=java.util.List.of();

    public java.util.List<String> getAllowedOrigins(){return allowedOrigins;}
    public  void setAllowedOrigins(java.util.List<String>allowedOrigins){this.allowedOrigins=allowedOrigins;}
}
