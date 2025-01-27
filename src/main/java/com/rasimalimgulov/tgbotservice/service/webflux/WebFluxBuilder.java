package com.rasimalimgulov.tgbotservice.service.webflux;

import com.rasimalimgulov.tgbotservice.entity.Client;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

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
    public List<Client> getClients(String username,String jwt) {
       return WebClient.create(urlApi)
                .get()
                .uri(uriBuilder -> uriBuilder.path("/clients").queryParam("username", username).build())
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Client>>() {})
                .block();}

    public List<ServiceTy>
}
