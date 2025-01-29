package com.rasimalimgulov.tgbotservice.service.webflux;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceTypeRequest {
    String username;
    String serviceTypeName;
}
