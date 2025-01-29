package com.rasimalimgulov.tgbotservice.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ExpenseCategory {

    private Long id;

    private String name;
}

