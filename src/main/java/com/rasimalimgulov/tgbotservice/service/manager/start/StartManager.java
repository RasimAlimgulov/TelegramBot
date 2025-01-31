package com.rasimalimgulov.tgbotservice.service.manager.start;

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

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    public StartManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        System.out.println("Выполняется метод answer(ответ) в классе StartManager");
        return methodFactory.getSendMessage(message.getChatId(),
                """
                          Добро пожаловать в наш бот! 🎉😊
                          Войдите в свой аккаунт для начала работы бота.
                        """, keyboardFactory.getInlineKeyboardMarkup(List.of("Войти"),
                        List.of(1),
                        List.of(LOGIN)));

    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String callbackData = callbackQuery.getData();
        switch (callbackData) {
            case MAIN_PAGE -> {
                return methodFactory.getSendMessage(chatId, String.format("Выберите дальнейшее действие: "),
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Добавить доход", "Добавить расход", "Просмотреть отчёт", "Настройки")
                                , List.of(2, 2), List.of(INCOME, OUTCOME, REPORT, SETTINGS)));
            }
        }
        return null;
    }
}
