package com.rasimalimgulov.tgbotservice.service.manager.money;

import com.rasimalimgulov.tgbotservice.dto.ServiceType;
import com.rasimalimgulov.tgbotservice.dto.TransactionType;
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

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MoneyManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public MoneyManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userSessionManager = userSessionManager;
        this.webFluxBuilder = webFluxBuilder;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        log.info("Выполняется MoneyManager");
        if (callbackData.contains("money_type") && session.getTransactionType().equals(TransactionType.EXPENSE)) {
            session.setMoneyType(MoneyType.valueOf(callbackData.split("_")[2].toUpperCase()));
            userSessionManager.updateSession(chatId, session);
            return answerMethodFactory.getSendMessage(chatId, "Успешно добавили тип оплаты",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Продолжить"), List.of(1), List.of(ADD_COMMENT)));

        } else if (callbackData.contains("money_type")) {
            session.setMoneyType(MoneyType.valueOf(callbackData.split("_")[2].toUpperCase()));
            userSessionManager.updateSession(chatId, session);
            return answerMethodFactory.getSendMessage(chatId, "Укажите статус транзакции"
                    , keyboardFactory.getInlineKeyboardMarkup(List.of("Предоплата", "Запланировано", "Финансовый долг", "Постоплата")
                            , List.of(2, 2), List.of("status_PREPAYMENT", "status_SCHEDULED", "status_FINANCIAL_DEBT", "status_POSTPAYMENT")
                    ));
        }
        switch (callbackData) {
            case MONEY_COUNT -> {
                session.setAwaitingAmountMoney(true);
                userSessionManager.updateSession(chatId, session);
                return answerMethodFactory.getSendMessage(chatId, "Введите сумму", null);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        if (session.isAwaitingAmountMoney()) {
            session.setAmountMoney(Double.valueOf(message.getText()));
            session.setAwaitingAmountMoney(false);
            userSessionManager.updateSession(chatId, session);
            log.info("Указали " + session.getAmountMoney());
            return answerMethodFactory.getSendMessage(chatId, "Выберите вид оплаты"
                    , keyboardFactory.getInlineKeyboardMarkup(List.of("Наличка", "Банк", "Карта"), List.of(3), List.of("money_type_cash", "money_type_bank", "money_type_card")));
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

}
