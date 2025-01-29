package com.rasimalimgulov.tgbotservice.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ServiceType {

    private Long id;

    private String name;

}

