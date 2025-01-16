package com.rasimalimgulov.tgbotservice.service.webflux;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebFluxBuilder {
    private static final String urlApi="http://localhost:8081/";

    public String userExists(String username, String password) {
        return WebClient.create(urlApi).post()
                .uri("/authentication")
                .bodyValue(new AuthRequest(username,password)).retrieve().bodyToMono(String.class).block();
    }
}
