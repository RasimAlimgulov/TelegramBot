package com.rasimalimgulov.tgbotservice.service.manager.session;

public class UserSession {
    private String username;
    private boolean awaitingLogin;
    private boolean awaitingPassword;
    private String jwt;

    public UserSession() {
        this.awaitingLogin = false;
        this.awaitingPassword = false;
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

    public void setAwaitingPassword(boolean awaitingPassword) {
        this.awaitingPassword = awaitingPassword;
    }
    public String getJwt() {return jwt; }
    public void setJwt(String jwt) {this.jwt = jwt;}
}

