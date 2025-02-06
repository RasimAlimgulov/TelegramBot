package com.rasimalimgulov.tgbotservice.telegram;

import com.rasimalimgulov.tgbotservice.service.UpdateDispatcher;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bot extends TelegramWebhookBot {

    final UpdateDispatcher updateDispatcher;

    final MyTelegramProperties myTelegramProperties;

    @Autowired
    public Bot(UpdateDispatcher updateDispatcher, MyTelegramProperties myTelegramProperties) {
        super(myTelegramProperties.getToken());
        this.updateDispatcher = updateDispatcher;
        this.myTelegramProperties = myTelegramProperties;

    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            return updateDispatcher.distribute(update,this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotPath() {
        return myTelegramProperties.getPath();
    }

    @Override
    public String getBotUsername() {
        return myTelegramProperties.getUsername();
    }

}
