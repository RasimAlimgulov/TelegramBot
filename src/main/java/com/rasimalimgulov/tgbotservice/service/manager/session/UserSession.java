package com.rasimalimgulov.tgbotservice.service.manager.session;

public class UserSession {
    private String username;
    private String jwt;

    private String newClientName;
    private String newClientPhone;



    private Integer amountMoney;
    private String category;
    private boolean awaitingLogin;
    private boolean awaitingPassword;

    private boolean awaitingNameNewClient;
    private boolean awaitingPhoneNewClient;

    private boolean awaitingServiceTypeNewClient;


    private boolean awaitingAmountMoney;
    private boolean awaitingCategory;

    public UserSession() {
    }
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAwaitingLogin() {
        return awaitingLogin;
    }

    public void setAwaitingLogin(boolean awaitingLogin) {
        this.awaitingLogin = awaitingLogin;
    }

    public boolean isAwaitingPassword() {
        return awaitingPassword;
    }

    public boolean isAwaitingAmountMoney() {
        return awaitingAmountMoney;
    }

    public void setAwaitingAmountMoney(boolean awaitingAmountMoney) {
        this.awaitingAmountMoney = awaitingAmountMoney;
    }

    public void setAwaitingPassword(boolean awaitingPassword) {
        this.awaitingPassword = awaitingPassword;
    }

    public Integer getAmountMoney() {
        return amountMoney;
    }

    public void setAmountMoney(Integer amountMoney) {
        this.amountMoney = amountMoney;
    }
    public boolean getAwaitingCategory() {
        return awaitingCategory;
    }
    public void setAwaitingCategory(boolean awaitingCategory) {
        this.awaitingCategory = awaitingCategory;
    }

    public boolean isAwaitingNameNewClient() {
        return awaitingNameNewClient;
    }

    public void setAwaitingNameNewClient(boolean awaitingNameNewClient) {
        this.awaitingNameNewClient = awaitingNameNewClient;
    }

    public boolean isAwaitingPhoneNewClient() {
        return awaitingPhoneNewClient;
    }

    public void setAwaitingPhoneNewClient(boolean awaitingPhoneNewClient) {
        this.awaitingPhoneNewClient = awaitingPhoneNewClient;
    }

    public String getNewClientName() {
        return newClientName;
    }

    public void setNewClientName(String newClientName) {
        this.newClientName = newClientName;
    }

    public String getNewClientPhone() {
        return newClientPhone;
    }

    public void setNewClientPhone(String newClientPhone) {
        this.newClientPhone = newClientPhone;
    }

    public boolean isAwaitingServiceTypeNewClient() {
        return awaitingServiceTypeNewClient;
    }

    public void setAwaitingServiceTypeNewClient(boolean awaitingServiceTypeNewClient) {
        this.awaitingServiceTypeNewClient = awaitingServiceTypeNewClient;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

