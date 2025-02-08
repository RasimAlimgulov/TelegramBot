package com.rasimalimgulov.tgbotservice.service.manager.session;

import com.rasimalimgulov.tgbotservice.dto.TransactionType;
import com.rasimalimgulov.tgbotservice.service.manager.money.MoneyType;
import com.rasimalimgulov.tgbotservice.service.manager.transaction.TransactionStatus;

import java.time.LocalDateTime;

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
    private boolean awaitingListServiceType;
    private MoneyType moneyType;
    private Long transaction_client_id;
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
    private String reportType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean awaitingCustomPeriod;
    private boolean awaitingNewLogin;
    private boolean awaitingNewPassword;
    private String comment;

    private String expenseCategory;
    private boolean awaitingAmountMoney;
    private boolean awaitingComment;
    private boolean awaitingExpenseCategory;

    public void cleanSessionMainPage() {

        this.setNewClientName(null);
        this.setNewClientPhone(null);
        this.setServiceTypeName(null);
        this.setAmountMoney(null);
        this.setAwaitingLogin(false);
        this.setAwaitingPassword(false);
        this.setAwaitingNameNewClient(false);
        this.setAwaitingPhoneNewClient(false);
        this.setAwaitingNewServiceType(false);
        this.setAwaitingListServiceType(false);
        this.setMoneyType(null);
        this.setTransaction_client_id(null);
        this.setTransactionStatus(null);
        this.setTransactionType(null);
        this.setReportType(null);
        this.setAwaitingCustomPeriod(false);
        this.setAwaitingNewLogin(false);
        this.setAwaitingNewPassword(false);
        this.setExpenseCategory(null);
        this.setAwaitingAmountMoney(false);
        this.setAwaitingComment(false);
        this.setAwaitingExpenseCategory(false);
        this.setTransactionStatus(null);

    }



    public boolean isAwaitingNewLogin() {
        return awaitingNewLogin;
    }

    public void setAwaitingNewLogin(boolean awaitingNewLogin) {
        this.awaitingNewLogin = awaitingNewLogin;
    }

    public boolean isAwaitingNewPassword() {
        return awaitingNewPassword;
    }

    public void setAwaitingNewPassword(boolean awaitingNewPassword) {
        this.awaitingNewPassword = awaitingNewPassword;
    }

    public boolean isAwaitingListServiceType() {
        return awaitingListServiceType;
    }

    public void setAwaitingListServiceType(boolean awaitingListServiceType) {
        this.awaitingListServiceType = awaitingListServiceType;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public void setTransactionDateRange(LocalDateTime start, LocalDateTime end) {
        this.startDate = start;
        this.endDate = end;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public boolean isAwaitingCustomPeriod() {
        return awaitingCustomPeriod;
    }

    public void setAwaitingCustomPeriod(boolean awaitingCustomPeriod) {
        this.awaitingCustomPeriod = awaitingCustomPeriod;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
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

