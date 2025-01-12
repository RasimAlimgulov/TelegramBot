package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.authentication.AuthenticationManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MessageHandler {
    final AuthenticationManager authenticationManager;

    public MessageHandler(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        return authenticationManager.answerMessage(message, bot);
    }
}
