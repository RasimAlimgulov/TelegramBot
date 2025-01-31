package com.rasimalimgulov.tgbotservice.dto;

import com.rasimalimgulov.tgbotservice.service.manager.money.MoneyType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransactionIncome {

    private TransactionType type;

    private Double amount;

//    private LocalDateTime transactionDate = LocalDateTime.now(); Тоже укажем на стороне клиента

    private Long clientId;

//    private ServiceType serviceType; Получим из объекта клиента

    private String status;

    private String comment;

    private String filePath;

    private MoneyType moneyType;

    private String username;
}
