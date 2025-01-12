package com.rasimalimgulov.tgbotservice.service.manager.authentication;

public class UserSession {
    private String login;
    private boolean awaitingLogin;
    private boolean awaitingPassword;

    public UserSession() {
        this.awaitingLogin = false;
        this.awaitingPassword = false;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public void setAwaitingPassword(boolean awaitingPassword) {
        this.awaitingPassword = awaitingPassword;
    }
}

