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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        bot.execute(answerMethodFactory.getAnswerCallbackQuery(callbackQuery));
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        if (callbackData.contains("serviceType_")){
            session.setAwaitingNewServiceType(false);
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
                session.setAwaitingNewServiceType(false);
                session.setServiceTypeName(newServiceType);
                session.setAwaitingListServiceType(true);
                userSessionManager.updateSession(chatId,session);
                log.info(serviceType);
            }
            catch (WebClientResponseException.BadRequest e){
                return answerMethodFactory.getSendMessage(chatId,"Тип услуги уже существует." +
                        " Отправьте новое название или выберите существующий \uD83D\uDC47."
                        ,keyboardFactory.getInlineKeyboardMarkup(List.of(newServiceType,"Главное меню"),List.of(2),List.of("serviceType_"+newServiceType,MAIN_PAGE)));
            }
            catch (Exception e){
                log.error(e);
                return answerMethodFactory.getSendMessage(chatId,"Произошла ошибка при добавлении типа услуг.",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));
            }


        return answerMethodFactory.getSendMessage(chatId,"Тип услуг добавлен успешно"
                    ,keyboardFactory.getInlineKeyboardMarkup(List.of("Выбрать тип услуг"),List.of(1),List.of(ADD_CLIENT_CONFIG)));

        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

}
