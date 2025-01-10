package com.rasimalimgulov.tgbotservice;

import com.rasimalimgulov.tgbotservice.telegram.MyTelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MyTelegramProperties.class)
public class BotServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotServiceApplication.class, args);
    }

}
