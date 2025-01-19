package com.rasimalimgulov.tgbotservice.service.manager.session;

public class UserSession {
    private String username;
    private String jwt;
    private Integer amountMoney;
    private String category;
    private boolean awaitingLogin;
    private boolean awaitingPassword;
    private boolean awaitingAmountMoney;
    private boolean awaitingCategory;


    public UserSession() {
        this.awaitingLogin = false;
        this.awaitingPassword = false;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

