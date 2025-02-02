package com.rasimalimgulov.tgbotservice.dto;

import com.rasimalimgulov.tgbotservice.service.manager.money.MoneyType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransactionOutcome {

    private TransactionType type;

    private Double amount;

//    private LocalDateTime transactionDate = LocalDateTime.now(); Тоже укажем на стороне клиента

    private String expenseCategory;

    private String comment;

    private MoneyType moneyType;

    private String username;

}
