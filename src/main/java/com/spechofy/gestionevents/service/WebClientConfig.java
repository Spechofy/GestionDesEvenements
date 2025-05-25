package com.spechofy.gestionevents.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient geolocationWebClient() {
        return WebClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org/search")
                .defaultHeader("User-Agent", "spechofy-geocoding-client")
                .build();
    }
}
