package com.rasimalimgulov.tgbotservice.service.webflux;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GetClientsRequest {
    Long userId;
}
