package com.rasimalimgulov.tgbotservice.service.webflux;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExpCatRequest {
   private String nameExpenseCategory;
   private String username;
}
