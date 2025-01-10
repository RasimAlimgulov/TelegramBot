package com.rasimalimgulov.tgbotservice.controller;

import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainController {

    final Bot myBot;

    public MainController(Bot myBot) {
        this.myBot = myBot;
    }

    @PostMapping("/")
    public BotApiMethod<?> listener(@RequestBody Update update) {
      return myBot.onWebhookUpdateReceived(update);
    }

}