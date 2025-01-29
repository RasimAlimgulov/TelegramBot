package com.rasimalimgulov.tgbotservice.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private Long id;
    private Long chatId;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDateTime registrationDate;
}

