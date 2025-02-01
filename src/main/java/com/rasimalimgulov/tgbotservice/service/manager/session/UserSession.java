package com.rasimalimgulov.tgbotservice.service.manager.session;

import com.rasimalimgulov.tgbotservice.service.manager.money.MoneyType;
import com.rasimalimgulov.tgbotservice.service.manager.transaction.TransactionStatus;

public class UserSession {
    private String username;
    private String jwt;

    private String newClientName;
    private String newClientPhone;
    private String serviceTypeName;


    private Double amountMoney;

    private boolean awaitingLogin;
    private boolean awaitingPassword;

    private boolean awaitingNameNewClient;
    private boolean awaitingPhoneNewClient;
    private boolean awaitingNewServiceType;
    private MoneyType moneyType;
    private Long transaction_client_id;
    private TransactionStatus transactionStatus;
    private String comment;

    private String expenseCategory;
    private boolean awaitingAmountMoney;
    private boolean awaitingComment;
    private boolean awaitingExpenseCategory;
    public UserSession() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public boolean isAwaitingComment() {
        return awaitingComment;
    }

    public void setAwaitingComment(boolean awaitingComment) {
        this.awaitingComment = awaitingComment;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
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

    public Double getAmountMoney() {
        return amountMoney;
    }

    public void setAmountMoney(Double amountMoney) {
        this.amountMoney = amountMoney;
    }
    public boolean getAwaitingExpenseCategory() {
        return awaitingExpenseCategory;
    }
    public void setAwaitingExpenseCategory(boolean awaitingExpenseCategory) {
        this.awaitingExpenseCategory = awaitingExpenseCategory;
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

    public boolean isAwaitingNewServiceType() {
        return awaitingNewServiceType;
    }

    public void setAwaitingNewServiceType(boolean awaitingNewServiceType) {
        this.awaitingNewServiceType = awaitingNewServiceType;
    }

    public MoneyType getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(MoneyType moneyType) {
        this.moneyType = moneyType;
    }

    public Long getTransaction_client_id() {
        return transaction_client_id;
    }

    public void setTransaction_client_id(Long transaction_client_id) {
        this.transaction_client_id = transaction_client_id;
    }

}

