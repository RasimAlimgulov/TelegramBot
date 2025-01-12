package com.rasimalimgulov.tgbotservice.service.manager.settings;

import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.AbstractManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingsManager extends AbstractManager {

    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    public SettingsManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(message.getChatId(),
                "Заглушка, тут будет меню настройки (была команда /settings)", null);
    }

    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return methodFactory.getEditMessageText(callbackQuery,
                "Была нажата кнопка settings, должно появиться меню настройки", null);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }
}
