package com.rasimalimgulov.tgbotservice.dto;

import lombok.*;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private Long id;
    private String fullName;
    private String phoneNumber;
}

