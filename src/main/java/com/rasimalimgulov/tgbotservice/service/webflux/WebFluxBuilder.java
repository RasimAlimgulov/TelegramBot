package com.rasimalimgulov.tgbotservice.service.webflux;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebFluxBuilder {
    private static final String urlApi = "http://localhost:8081/";

    public String authenticateRequest(String username, String password) {
        return WebClient.create(urlApi).post()
                .uri("/authentication")
                .bodyValue(new AuthRequest(username, password)).retrieve().bodyToMono(String.class).block();
    }

    public boolean incomeRequest(Long chatId, String jwt, Integer amountMoney) {
        return WebClient.create(urlApi).post()
                .uri("/income")
                .bodyValue(new IncomeRequest(chatId,amountMoney)).header("Authorization","Bearer "+jwt).retrieve().bodyToMono(Boolean.class).block();
    }
}
