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
public class TransactionIOManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public TransactionIOManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
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
        String[] transactionStatus=callbackData.split("_");
        if (callbackData.contains("status_")) {
            session.setTransactionStatus(TransactionStatus.valueOf(transactionStatus.length==2?transactionStatus[1]:transactionStatus[1]+"_"+transactionStatus[2]));
            session.setAwaitingComment(true);
            userSessionManager.updateSession(chatId,session);
            return answerMethodFactory.getSendMessage(chatId,"Оставьте коментарий:",null);
        }

        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId=message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        if (session.isAwaitingComment()){
            session.setComment(message.getText());
            userSessionManager.updateSession(chatId,session);

            log.info("client_id="+session.getTransaction_client_id()+" money_count="+session.getAmountMoney()
                    +" тип денег="+session.getMoneyType()+" тип услуги="+session.getServiceTypeName()
                    +" статус транзакции="+session.getTransactionStatus()+" Комментарий="+session.getComment());
            return answerMethodFactory.getSendMessage(chatId,"Подтвердите создание дохода: "+"client_id="+session.getTransaction_client_id()+" money_count="+session.getAmountMoney()
                            +" тип денег="+session.getMoneyType()+" тип услуги="+session.getServiceTypeName()
                            +" статус транзакции="+session.getTransactionStatus()+" Комментарий="+session.getComment(),
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Подтвердить"),List.of(1),List.of(TRANSACTION_INCOME_REQUEST)));
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

}
