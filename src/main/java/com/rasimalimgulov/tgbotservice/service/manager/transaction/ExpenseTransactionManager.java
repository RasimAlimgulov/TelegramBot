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
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseTransactionManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public ExpenseTransactionManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
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

        switch (callbackData) {
            case TRANSACTION_OUTCOME_REQUEST -> {
                Object transactionResult = null;
                try {
                    log.info("Выполняется try catch");
                    transactionResult = webFluxBuilder.addNewTransactionOutcome(session);
                } catch (Exception e) {
                    log.info("Ошибка возникла при отправке запроса на транзакцию расхода" + e.getMessage() + e.getCause());
                    return answerMethodFactory.getSendMessage(chatId, "Не получилось отправить транзакцию", null);
                }
                log.info(transactionResult);
                UserSession cleanSession=cleanSession(session); ////// Очищаем сессию
                userSessionManager.updateSession(chatId, cleanSession);
                return answerMethodFactory.getSendMessage(chatId, "Поздравляю, мы успешно сохранили расход.Выберите дальнейшее действие:",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Добавить еще один расход", "Главное меню"), List.of(2), List.of(OUTCOME, MAIN_PAGE)));
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

    private UserSession cleanSession(UserSession session) {
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
