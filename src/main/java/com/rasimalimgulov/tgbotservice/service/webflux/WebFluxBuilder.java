package com.rasimalimgulov.tgbotservice.service.webflux;

import com.rasimalimgulov.tgbotservice.dto.*;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
@Log4j2
@Component
public class WebFluxBuilder {
    private static final String urlApi = "http://localhost:8081/";

    public String authenticateRequest(String username, String password) {
        return WebClient.create(urlApi).post()
                .uri("/authentication")
                .bodyValue(new AuthRequest(username, password)).retrieve().bodyToMono(String.class).block();
    }

    public boolean incomeRequest(Long chatId, String jwt, Double amountMoney) {
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
    public List<ExpenseCategory> getExpenseCategories(String username, String jwt) {
        return WebClient.create(urlApi)
                .get()
                .uri(uriBuilder -> uriBuilder.path("/expensecategories").queryParam("username", username).build())
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ExpenseCategory>>() {
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

    public Client addNewClient(String username, String nameClient, String phoneClient, String serviceType, String jwt) {
        log.info("Выполняется метод по запросу по добавлению клиента. ServiceType="+serviceType);
        return WebClient.create(urlApi)
                .post()
                .uri("/addclient").bodyValue(new NewClientRequest(username,nameClient,phoneClient,serviceType))
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(Client.class).block();
    }
    public TransactionIncome addNewTransactionIncome(UserSession session) {

        return WebClient.create(urlApi)
                .post()
                .uri("/addincome").bodyValue(new TransactionIncome(
                        TransactionType.INCOME
                        ,session.getAmountMoney()
                        ,session.getTransaction_client_id()
                        ,session.getTransactionStatus().toString(),session.getComment(),null,session.getMoneyType(),session.getUsername()))
                .header("Authorization", "Bearer " + session.getJwt())
                .retrieve()
                .bodyToMono(TransactionIncome.class).block();
    }
    public TransactionOutcome addNewTransactionOutcome(UserSession session) {
        return WebClient.create(urlApi)
                .post()
                .uri("/addoutcome").bodyValue(new TransactionOutcome(
                        TransactionType.EXPENSE
                        ,session.getAmountMoney()
                        ,session.getExpenseCategory()
                        ,session.getComment(),session.getMoneyType(),session.getUsername()))
                .header("Authorization", "Bearer " + session.getJwt())
                .retrieve()
                .bodyToMono(TransactionOutcome.class).block();
    }


    public ExpenseCategory addNewExpenseCategory(String nameExpenseCategory,String username, String jwt) {
        return WebClient.create(urlApi)
                .post()
                .uri("/addexpensecategory").bodyValue(new ExpCatRequest(nameExpenseCategory,username))
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(ExpenseCategory.class).block();
    }

    //////////////////////////////////////////////////////////////////////////   ОТЧЕТЫ

    public byte[] downloadIncomeReportByClients(String username, String jwt) {
        return WebClient.create(urlApi)
                .get()
                .uri("/report/income-by-clients?username=" + username)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    public byte[] downloadIncomeReportByCategories(String username, String jwt) {
        return WebClient.create(urlApi)
                .get()
                .uri("/report/income-by-categories?username=" + username)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    public byte[] downloadExpenseReportByCategories(String username, String jwt) {
        return WebClient.create(urlApi)
                .get()
                .uri("/report/expenses-by-categories?username=" + username)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    public byte[] downloadTotalReport(String username, String jwt) {
        return WebClient.create(urlApi)
                .get()
                .uri("/report/total?username=" + username)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

}
