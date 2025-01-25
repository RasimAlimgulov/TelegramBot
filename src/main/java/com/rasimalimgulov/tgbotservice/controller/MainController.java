package com.rasimalimgulov.tgbotservice.controller;

import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainController {

    final Bot myBot;

    public MainController(Bot myBot) {
        this.myBot = myBot;
    }

    @PostMapping("/")
    public BotApiMethod<?> listener(@RequestBody Update update) {
        System.out.println("Получаем запрос из телеграмма в контроллере: "+update.getMessage());
        return myBot.onWebhookUpdateReceived(update);
    }

}
