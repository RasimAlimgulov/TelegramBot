package com.rasimalimgulov.tgbotservice.service.webflux;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@ToString
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncomeRequest {
    Long chatId;
    Integer amountMoney;

    public IncomeRequest(Long chatId, Integer amountMoney) {
        this.chatId = chatId;
        this.amountMoney = amountMoney;
    }
}
