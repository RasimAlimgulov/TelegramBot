package com.rasimalimgulov.tgbotservice.service.manager;

import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingsManager {

    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    public SettingsManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    public BotApiMethod<?> settingsCommand(Message message) {
        return methodFactory.getSendMessage(message.getChatId(),
                "Заглушка, тут будет меню настройки (была команда /settings)", null);
    }
    public BotApiMethod<?> settingsCallbackQuery(CallbackQuery callbackQuery) {
        return methodFactory.getEditMessageText(callbackQuery,
                "Была нажата кнопка settings, должно появиться меню настройки", null);
    }
}
