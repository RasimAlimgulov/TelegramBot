package com.rasimalimgulov.tgbotservice.service.webflux;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeLoginRequest {
    String username;
    String newUsername;
}
