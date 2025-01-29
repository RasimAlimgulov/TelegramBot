package com.rasimalimgulov.tgbotservice.service.manager.client;

import com.rasimalimgulov.tgbotservice.dto.Client;
import com.rasimalimgulov.tgbotservice.dto.ServiceType;
import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.AbstractManager;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSession;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSessionManager;
import com.rasimalimgulov.tgbotservice.service.webflux.NewClientRequest;
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
public class ClientManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public ClientManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
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
            case ADD_CLIENT_CONFIG ->{
                  session.setAwaitingNameNewClient(true);
                  userSessionManager.updateSession(chatId,session);
                  return answerMethodFactory.getSendMessage(chatId,"Введите Ф.И.О клиента",null);
            }
            case ADD_CLIENT_REQUEST -> {
                String username=session.getUsername();
                String name= session.getNewClientName();
               String phone= session.getNewClientPhone();
               String serviceType= session.getServiceTypeName();
               String jwt=session.getJwt();
                Client client=null;
               try {
                   client=webFluxBuilder.addNewClient(username,name,phone,serviceType,jwt);
               }catch (Exception e){
                   log.info(e.getMessage());
                   return answerMethodFactory.getSendMessage(chatId,e.getMessage(),null);
               }
               log.info("Добавили нового клиента в БД: "+client);
               session.setAwaitingAmountMoney(true);
               userSessionManager.updateSession(chatId,session);
               return answerMethodFactory.getSendMessage(chatId,"Успешно добавили нового клиента: "+client
                       , keyboardFactory.getInlineKeyboardMarkup(List.of("Указать сумму прибыли"),List.of(1),List.of(MONEY_COUNT)));
            }
            case MONEY_COUNT -> {
                session.setAwaitingAmountMoney(true);
                userSessionManager.updateSession(chatId,session);
                return answerMethodFactory.getSendMessage(chatId,"Введите сумму",null);
            }

        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        if (session.isAwaitingNameNewClient()){
            String nameNewClient=message.getText();
            session.setNewClientName(nameNewClient);
            session.setAwaitingNameNewClient(false);
            session.setAwaitingPhoneNewClient(true);
            userSessionManager.updateSession(chatId,session);
            return answerMethodFactory.getSendMessage(chatId,"Теперь введите номер телефона клиента",null);
        }
        if (session.isAwaitingPhoneNewClient()){
            String phoneNewClient=message.getText();
            session.setNewClientPhone(phoneNewClient);
            session.setAwaitingPhoneNewClient(false);
//            session.setAwaitingServiceTypeNewClient(true);
            userSessionManager.updateSession(chatId,session);
            List<ServiceType> serviceTypes=new ArrayList<>();
            try {
                serviceTypes=webFluxBuilder.getServiceTypesByUsername(session.getUsername(), session.getJwt());
            }catch (Exception e){
                log.error(e.getMessage());
            }
            if (serviceTypes.isEmpty()){
                return answerMethodFactory.getSendMessage(chatId,"У вас нет типов услуг. Добавьте новый тип услуги."
                        ,keyboardFactory.getInlineKeyboardMarkup(List.of("Добавить"),List.of(1),List.of(ADD_TYPE_SERVICE)));
            }

            List<String> typesService = serviceTypes.stream()
                    .map(ServiceType::getName)
                    .collect(Collectors.toList());
            typesService.add("Добавить");
            List<String> typesServiceCallbacks = serviceTypes.stream()
                    .map(serviceType -> "serviceType_" + serviceType.getId()) // Используем ID ServiceType для уникального callback
                    .collect(Collectors.toList());

            typesServiceCallbacks.add(ADD_TYPE_SERVICE);

            return answerMethodFactory.getSendMessage(
                    chatId,
                    "Выберите тип услуги из списка или создайте новый:",
                    keyboardFactory.getInlineKeyboardMarkup(
                            typesService,
                            List.of(typesService.size()),
                            typesServiceCallbacks
                    )
            );
        }
        if (session.isAwaitingAmountMoney()){

        }
        return null ;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

}
