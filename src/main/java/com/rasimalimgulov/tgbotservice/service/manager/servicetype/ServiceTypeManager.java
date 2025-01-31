package com.rasimalimgulov.tgbotservice.service.manager.servicetype;

import com.rasimalimgulov.tgbotservice.dto.ServiceType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceTypeManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public ServiceTypeManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
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
        if (callbackData.contains("serviceType_")){
            session.setServiceTypeName(callbackData.split("_")[1]);
            userSessionManager.updateSession(chatId,session);
            return answerMethodFactory.getSendMessage(chatId,"Тип данных добавлен успешно. Имя клиента: " +
                            ""+session.getNewClientName()+" Номер телефона: "+session.getNewClientPhone()+" Тип услуги: "+session.getServiceTypeName()
                    ,keyboardFactory.getInlineKeyboardMarkup(List.of("Подтвердить"),List.of(1),List.of(ADD_CLIENT_REQUEST)));
        }
        switch (callbackData) {
            case ADD_TYPE_SERVICE ->{
                  session.setAwaitingNewServiceType(true);
                  userSessionManager.updateSession(chatId,session);
                  return answerMethodFactory.getSendMessage(chatId,"Введите название нового типа услуги",null);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        if (session.isAwaitingNewServiceType()) {
            String newServiceType=message.getText();
           ServiceType serviceType=null;
            try {
                serviceType=webFluxBuilder.addNewServiceType(session.getUsername(),newServiceType,session.getJwt());
            }catch (Exception e){
                log.error(e);
                return answerMethodFactory.getSendMessage(chatId,"Ошибка при сохранении нового типа услуги",null);
            }
            session.setAwaitingNewServiceType(false);
            session.setServiceTypeName(newServiceType);
            userSessionManager.updateSession(chatId,session);
            log.info(serviceType);

        return answerMethodFactory.getSendMessage(chatId,"Тип данных добавлен успешно. Имя клиента: " +
                        ""+session.getNewClientName()+" Номер телефона: "+session.getNewClientPhone()+" Тип услуги: "+session.getServiceTypeName()
                ,keyboardFactory.getInlineKeyboardMarkup(List.of("Подтвердить"),List.of(1),List.of(ADD_CLIENT_REQUEST)));
    }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

}
