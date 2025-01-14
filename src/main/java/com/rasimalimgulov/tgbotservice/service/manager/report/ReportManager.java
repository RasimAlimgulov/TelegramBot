package com.rasimalimgulov.tgbotservice.service.manager.report;

import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.AbstractManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;

    public ReportManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(message.getChatId(),
                "Заглушка, тут будет отчет наверное (была команда /report)", null);
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        log.info("Выполняется метод answer в классе ReportManager");

        String callbackData = callbackQuery.getData();
        System.out.println("callbackData= " + callbackData);

        switch (callbackData) {
            case INCOME:
                return methodFactory.getSendMessage(callbackQuery.getMessage().getChatId(),
                        "Введите сумму",
                        null);

            case OUTCOME:
                return methodFactory.getEditMessageText(callbackQuery,
                        "Выберите категорию",
                        keyboardFactory.getInlineKeyboardMarkup(
                                List.of("Зарплата", "Реклама", "Налоги", "Бытовые расходы", "Комиссия", "Назад"),
                                List.of(3, 2),
                                List.of(CATEGORY_SALARY, CATEGORY_ADS, CATEGORY_TAX, CATEGORY_EXPENSE, CATEGORY_COMMISSION, LOGIN)
                        ));
            case REPORT:
                return methodFactory.getEditMessageText(callbackQuery,
                        "За какой период вы хотите получить отчет?",
                        keyboardFactory.getInlineKeyboardMarkup(
                                List.of("Сегодня", "За неделю", "За месяц", "Указать гггг-мм-дд","Назад"),
                                List.of(3, 2),
                                List.of(TODAY, WEEK, MONTH, USER_DATE,LOGIN)
                        ));
            default: return null;
        }

    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }
}
