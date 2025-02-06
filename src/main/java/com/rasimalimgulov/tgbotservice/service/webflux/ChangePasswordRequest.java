package com.rasimalimgulov.tgbotservice.service.webflux;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {
    String username;
    String password;
}
