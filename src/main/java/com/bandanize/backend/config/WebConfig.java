package com.bandanize.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

    @org.springframework.beans.factory.annotation.Value("${storage.location}")
    private String storageLocation;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Override
    public void addResourceHandlers(
            org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        String location = storageLocation;
        if (location == null || location.trim().isEmpty()) {
            location = "uploads";
        }

        // Ensure trailing slash
        if (!location.endsWith("/")) {
            location += "/";
        }

        // Map /uploads/** (legacy) and /api/uploads/** (new) to the file system
        // directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(java.nio.file.Paths.get(location).toUri().toString());
        registry.addResourceHandler("/api/uploads/**")
                .addResourceLocations(java.nio.file.Paths.get(location).toUri().toString());
    }
}