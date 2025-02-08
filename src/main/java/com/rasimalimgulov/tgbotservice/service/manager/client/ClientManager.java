package com.rasimalimgulov.tgbotservice.service.manager.client;

import com.rasimalimgulov.tgbotservice.dto.Client;
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
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientManager extends AbstractManager {
    AnswerMethodFactory answerMethodFactory;
    KeyboardFactory keyboardFactory;
    UserSessionManager userSessionManager;
    WebFluxBuilder webFluxBuilder;

    public ClientManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory,
                         UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userSessionManager = userSessionManager;
        this.webFluxBuilder = webFluxBuilder;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        bot.execute(answerMethodFactory.getAnswerCallbackQuery(callbackQuery));
        Long chatId = callbackQuery.getMessage().getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        String callbackData = callbackQuery.getData();

        if (callbackData.startsWith("client_")) {
            return handleClientSelection(chatId, session, callbackData);
        }
        return switch (callbackData) {
            case ADD_CLIENT_CONFIG -> handleNewClientConfig(chatId, session);
            case ADD_CLIENT_REQUEST -> handleNewClientRequest(chatId, session);
            default -> null;
        };
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        String text = message.getText();

        if (session.isAwaitingNameNewClient()) {
            return handleClientNameInput(chatId, session, text);
        }
        if (session.isAwaitingPhoneNewClient()) {
            Optional<Client> clientByPhone= webFluxBuilder.checkPhoneClient(session.getUsername(),text,session.getJwt());
            if (!clientByPhone.isEmpty()) {
                return answerMethodFactory.getSendMessage(chatId,"Клиент с таким номером уже существует." +
                        " Отправьте другой номер или выберите существующего \uD83D\uDC47.",
                        keyboardFactory.getInlineKeyboardMarkup(List.of(clientByPhone.get().getFullName(),"Главное меню"),List.of(1,1),List.of("client_"+clientByPhone.get().getId(),MAIN_PAGE))
                      );
            }
            //////////////////Здесь делаю запрос на проверку существования клиента с таким номером. Если существует прошу ввести другой номер или использовать уже существующего.
            return handleClientPhoneInput(chatId, session, text);
        }
        return null;
    }

    private BotApiMethod<?> handleClientSelection(Long chatId, UserSession session, String callbackData) {
        session.setAwaitingPhoneNewClient(false);
        session.setAwaitingAmountMoney(true);
        session.setTransaction_client_id(Long.parseLong(callbackData.split("_")[1]));
        return answerMethodFactory.getSendMessage(chatId, "Укажите сумму прибыли", null);
    }

    private BotApiMethod<?> handleNewClientConfig(Long chatId, UserSession session) {
        if (session.isAwaitingListServiceType()) {
            session.setAwaitingListServiceType(false);
            return generateServiceTypeSelection(chatId,session);
        }
        session.setAwaitingNameNewClient(true);
        userSessionManager.updateSession(chatId, session);
        return answerMethodFactory.getSendMessage(chatId, "Введите Ф.И.О клиента", null);
    }

    private BotApiMethod<?> handleNewClientRequest(Long chatId, UserSession session) {
        log.info("ServiceType в методе addClient в ClientManager:"+session.getServiceTypeName());
        try {
            Client client = webFluxBuilder.addNewClient(
                    session.getUsername(),
                    session.getNewClientName(),
                    session.getNewClientPhone(),
                    session.getServiceTypeName(),
                    session.getJwt()
            );
            log.info("Добавили нового клиента в БД: {}", client);
            session.setTransaction_client_id(client.getId());
            session.setNewClientName(null);
            session.setNewClientPhone(null);
            session.setServiceTypeName(null);
            session.setAwaitingAmountMoney(true);
            userSessionManager.updateSession(chatId, session);
            return answerMethodFactory.getSendMessage(chatId, "Успешно добавили нового клиента: " + client,
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Указать сумму прибыли"), List.of(1), List.of(MONEY_COUNT)));
        }catch (WebClientResponseException.Unauthorized e){
            return answerMethodFactory.getSendMessage(chatId,"У вас закончилась сессия. Чтобы продолжить работу войдите в свой аккаунт.",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Войти"),List.of(1),List.of(MAIN_PAGE)));
        }
        catch (WebClientResponseException.BadRequest e ) {
            log.error("Ошибка при добавлении клиента: {}", e.getMessage());
            return answerMethodFactory.getSendMessage(chatId,e.getResponseBodyAsString(),
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));
        }
    }

    private BotApiMethod<?> handleClientNameInput(Long chatId, UserSession session, String name) {
        session.setNewClientName(name);
        session.setAwaitingNameNewClient(false);
        session.setAwaitingPhoneNewClient(true);
        userSessionManager.updateSession(chatId, session);
        return answerMethodFactory.getSendMessage(chatId, "Теперь введите номер телефона клиента", null);
    }

    private BotApiMethod<?> handleClientPhoneInput(Long chatId, UserSession session, String phone) {
        session.setNewClientPhone(phone);
        session.setAwaitingPhoneNewClient(false);
        userSessionManager.updateSession(chatId, session);
        return generateServiceTypeSelection(chatId, session);
    }

    private BotApiMethod<?> generateServiceTypeSelection(Long chatId, UserSession session) {
        List<ServiceType> serviceTypes;
        try {
            serviceTypes = webFluxBuilder.getServiceTypesByUsername(session.getUsername(), session.getJwt());
        } catch (Exception e) {
            log.error("Ошибка при получении типов услуг: {}", e.getMessage());
            return answerMethodFactory.getSendMessage(chatId,"Произошла ошибка при получении типов услуг.",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));
        }
        if (serviceTypes.isEmpty()) {
            return answerMethodFactory.getSendMessage(chatId, "У вас нет типов услуг. Добавьте новый тип услуги.",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Добавить"), List.of(1), List.of(ADD_TYPE_SERVICE)));
        }
        List<String> typeNames = serviceTypes.stream().map(ServiceType::getName).collect(Collectors.toList());
        List<String> typeCallbacks = serviceTypes.stream()
                .map(serviceType -> "serviceType_" + "_" + serviceType.getName())
                .collect(Collectors.toList());
        typeNames.add("Добавить");
        typeCallbacks.add(ADD_TYPE_SERVICE);
        List<Integer> rows=new ArrayList<>();
        for (int i = 0; i < typeNames.size(); i++) {
            rows.add(1);
        }
        return answerMethodFactory.getSendMessage(chatId, "Выберите тип услуги из списка или создайте новый:",
                keyboardFactory.getInlineKeyboardMarkup(typeNames, rows, typeCallbacks));
    }
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }
}

