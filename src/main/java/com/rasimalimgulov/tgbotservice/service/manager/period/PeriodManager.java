package com.rasimalimgulov.tgbotservice.service.manager.period;


import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.AbstractManager;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSession;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSessionManager;
import com.rasimalimgulov.tgbotservice.service.webflux.WebFluxBuilder;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PeriodManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public PeriodManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userSessionManager = userSessionManager;
        this.webFluxBuilder = webFluxBuilder;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        LocalDateTime now = LocalDateTime.now();
        switch (callbackData) {
            case DAY -> {session.setTransactionDateRange(now.minusDays(1), now);}
            case WEEK -> session.setTransactionDateRange(now.minusWeeks(1), now);
            case MONTH -> session.setTransactionDateRange(now.minusMonths(1), now);
            case USER_PERIOD -> {
                session.setAwaitingCustomPeriod(true); // Ожидаем ввод пользователя
                return methodFactory.getSendMessage(chatId, "Введите период в формате YYYY-MM-DD - YYYY-MM-DD:",null);
            }
            case DETAILS -> {
             switch (session.getReportType()){
                 case INCOME_REPORT-> {
                     return methodFactory.getSendMessage(chatId,"Какой уровень детализации вам нужен?",
                             keyboardFactory.getInlineKeyboardMarkup(List.of("По клиентам","По категориям доходов"),List.of(2),List.of(INCOME_REPORT_BY_CLIENT,INCOME_REPORT_BY_CATEGORY)));
                 }
                 case EXPENSE_REPORT->{
                     return methodFactory.getSendMessage(chatId,"Какой уровень детализации вам нужен?",
                             keyboardFactory.getInlineKeyboardMarkup(List.of("По категориям расходов"),List.of(1),List.of(EXPENSE_REPORT_BY_CATEGORY)));
                 }
                 case ALL_REPORT -> {
                     return methodFactory.getSendMessage(chatId,"Какой уровень детализации вам нужен?",
                             keyboardFactory.getInlineKeyboardMarkup(List.of("Общая сумма"),List.of(1),List.of(ALL_REPORT_TOTAL)));
                 }
             }
            }
        }

        return methodFactory.getSendMessage(chatId,"Отлично, период сохранен",keyboardFactory.getInlineKeyboardMarkup(
                List.of("Продолжить"),
                List.of(1),
                List.of(DETAILS)));
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session=userSessionManager.getSession(chatId);
        if (session.isAwaitingCustomPeriod()) {
            String text = message.getText();
            try {
                String[] dates = text.split(" - ");
                LocalDate startDate = LocalDate.parse(dates[0]);
                LocalDate endDate = LocalDate.parse(dates[1]);
                session.setTransactionDateRange(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
                session.setAwaitingCustomPeriod(false);
                return methodFactory.getSendMessage(chatId, "Вы выбрали период: " + text,
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Продолжить"),List.of(1),List.of(DETAILS)));
            } catch (Exception e) {
                return methodFactory.getSendMessage(message.getChatId(), "Ошибка! Введите даты в формате YYYY-MM-DD - YYYY-MM-DD.",null);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(message.getChatId(), "Заглушка, тут будет отчет наверное (была команда /report)", null);
    }
}

