package com.rasimalimgulov.tgbotservice;

import com.rasimalimgulov.tgbotservice.telegram.MyTelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableConfigurationProperties(MyTelegramProperties.class)
@EnableAspectJAutoProxy
public class BotServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotServiceApplication.class, args);
    }

}
