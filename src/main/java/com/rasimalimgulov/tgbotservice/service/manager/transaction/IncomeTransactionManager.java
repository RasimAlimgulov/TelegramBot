package com.rasimalimgulov.tgbotservice.service.manager.transaction;

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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncomeTransactionManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public IncomeTransactionManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
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
        String[] transactionStatus = callbackData.split("_");
        if (callbackData.contains("status_")) {
            session.setTransactionStatus(TransactionStatus.valueOf(transactionStatus.length == 2 ? transactionStatus[1] : transactionStatus[1] + "_" + transactionStatus[2]));
            session.setAwaitingComment(true);
            userSessionManager.updateSession(chatId, session);
            return answerMethodFactory.getSendMessage(chatId, "Статус успешно добавлен.",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Продолжить"), List.of(1), List.of(ADD_COMMENT)));
        }

        switch (callbackData) {
            case TRANSACTION_INCOME_REQUEST -> {
                Object transactionResult = null;
                try {
                    transactionResult = webFluxBuilder.addNewTransactionIncome(session);
                }
                catch (WebClientResponseException.Unauthorized e) {
                    return answerMethodFactory.getSendMessage(chatId, "У вас закончилась сессия. Чтобы продолжить работу войдите в свой аккаунт.",
                            keyboardFactory.getInlineKeyboardMarkup(List.of("Войти"), List.of(1), List.of(LOGIN)));
                } catch (Exception e) {
                    log.info(e.getMessage());
                    return answerMethodFactory.getSendMessage(chatId, "Произошла ошибка при отправке запроса на транзакцию прибыли.",
                            keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"), List.of(1), List.of(MAIN_PAGE)));
                }
                log.info(transactionResult);
                UserSession cleanSession = cleansession(session); ////// Очищаем сессию
                userSessionManager.updateSession(chatId, cleanSession);
                return answerMethodFactory.getSendMessage(chatId, "Поздравляю, мы успешно сохранили доход.Выберите дальнейшее действие:",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Добавить еще один доход", "Главное меню"), List.of(2), List.of(INCOME, MAIN_PAGE)));
            }
        }

        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    private UserSession cleansession(UserSession session) {
        session.setExpenseCategory(null);
        session.setNewClientName(null);
        session.setNewClientPhone(null);
        session.setServiceTypeName(null);
        session.setMoneyType(null);
        session.setTransactionStatus(null);
        session.setAmountMoney(null);
        session.setTransaction_client_id(null);
        session.setComment(null);
        session.setTransactionType(null);
        return session;
    }
}
