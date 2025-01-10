package com.rasimalimgulov.tgbotservice.service.manager;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SettingsManager {

    public BotApiMethod<?> settingsCommand(Message message) {
        return SendMessage.builder().chatId(message.getChatId()).text("Скоро мы добавим настройки. \n По идее должны появиться варианты настроек.").build();
    }
    public BotApiMethod<?> settingsCallbackQuery(CallbackQuery callbackQuery) {
        return EditMessageText.builder().chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text("Скоро мы добавим настройки. \n По идее должны появиться варианты настроек.").build();
    }
}
