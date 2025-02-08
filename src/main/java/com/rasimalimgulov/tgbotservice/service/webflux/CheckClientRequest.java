package com.rasimalimgulov.tgbotservice.service.webflux;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckClientRequest {
private String username;
private String phone;
}
