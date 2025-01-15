package com.rasimalimgulov.tgbotservice.service.webflux;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequest {
    String username;
    String password;
    public AuthRequest(String username, String password) {this.username = username; this.password = password;}
}
