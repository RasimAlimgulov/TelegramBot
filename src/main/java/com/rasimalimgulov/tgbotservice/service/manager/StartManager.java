package com.rasimalimgulov.tgbotservice.service.manager;

import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.REPORT;
import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.SETTINGS;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    public StartManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    public BotApiMethod<?> startCommand(Message message) {
        return methodFactory.getSendMessage(message.getChatId(),
                """
                          Добро пожаловать в наш бот! 🎉😊
                          Здесь вы можете найти много полезной информации !Если у вас есть вопросы, просто напишите нам.
                          Мы всегда рады помочь ! 🤖✨
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Мои отчёты", "Настройки"),
                        List.of(2),
                        List.of(REPORT, SETTINGS)
                ));
    }
}
