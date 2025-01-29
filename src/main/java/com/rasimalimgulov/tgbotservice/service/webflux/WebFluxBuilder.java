package com.rasimalimgulov.tgbotservice.service.webflux;

import com.rasimalimgulov.tgbotservice.dto.Client;
import com.rasimalimgulov.tgbotservice.dto.ServiceType;
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
                .bodyValue(new IncomeRequest(chatId, amountMoney)).header("Authorization", "Bearer " + jwt).retrieve().bodyToMono(Boolean.class).block();
    }

    public List<Client> getClients(String username, String jwt) {
        return WebClient.create(urlApi)
                .get()
                .uri(uriBuilder -> uriBuilder.path("/clients").queryParam("username", username).build())
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Client>>() {
                })
                .block();
    }

    public List<ServiceType> getServiceTypesByUsername(String username, String jwt) {
        return WebClient.create(urlApi)
                .get()
                .uri(uriBuilder -> uriBuilder.path("/servicetypes").queryParam("username", username).build())
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ServiceType>>() {
                })
                .block();
    }

    public ServiceType addNewServiceType(String username, String nameServiceType, String jwt) {
        return WebClient.create(urlApi)
                .post()
                .uri("/addservicetype").bodyValue(new ServiceTypeRequest(username, nameServiceType))
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(ServiceType.class).block();
    }

    public Client addNewClient(String username,String nameClient,String phoneClient,String serviceType,String jwt) {
        return WebClient.create(urlApi)
                .post()
                .uri("/addclient").bodyValue(new NewClientRequest(username,nameClient,phoneClient,serviceType))
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(Client.class).block();
    }
}
