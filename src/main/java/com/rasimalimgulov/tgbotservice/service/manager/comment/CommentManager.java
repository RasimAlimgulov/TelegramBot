package com.rasimalimgulov.tgbotservice.service.manager.comment;

import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.AbstractManager;
import com.rasimalimgulov.tgbotservice.service.manager.money.MoneyType;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public CommentManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userSessionManager = userSessionManager;
        this.webFluxBuilder = webFluxBuilder;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        bot.execute(answerMethodFactory.getAnswerCallbackQuery(callbackQuery));
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        switch (callbackData) {
            case ADD_COMMENT -> {
                session.setAwaitingComment(true);
                return answerMethodFactory.getSendMessage(chatId, "Оставьте комментарий", null);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        if (session.isAwaitingComment()) {
            String comment = message.getText();
            session.setAwaitingComment(false);
            session.setComment(comment);
            switch (session.getTransactionType()) {
                case INCOME -> {
                    return answerMethodFactory.getSendMessage(chatId, "Подтвердите создание дохода: " + "client_id=" + session.getTransaction_client_id() + " money_count=" + session.getAmountMoney()
                                    + " тип денег=" + session.getMoneyType() + " тип услуги=" + session.getServiceTypeName()
                                    + " статус транзакции=" + session.getTransactionStatus() + " Комментарий=" + session.getComment(),
                            keyboardFactory.getInlineKeyboardMarkup(List.of("Подтвердить"), List.of(1), List.of(TRANSACTION_INCOME_REQUEST)));
                }
                case EXPENSE -> {
                    return answerMethodFactory.getSendMessage(chatId, "Подтвердите создание расхода: " + "client_id=" + session.getTransaction_client_id() + " money_count=" + session.getAmountMoney()
                                    + " тип денег=" + session.getMoneyType() + "категория расхода" + session.getExpenseCategory()
                                    + " Комментарий=" + session.getComment(),
                            keyboardFactory.getInlineKeyboardMarkup(List.of("Подтвердить"), List.of(1), List.of(TRANSACTION_OUTCOME_REQUEST)));

                }
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

}
