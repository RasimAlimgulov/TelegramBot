package com.rasimalimgulov.tgbotservice.service.manager.report;

import com.rasimalimgulov.tgbotservice.dto.Client;
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
import java.util.stream.Collectors;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public ReportManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
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

        switch (callbackData) {
            case INCOME:
                return incomeMethod(callbackQuery, chatId, session);

            case OUTCOME:
                return methodFactory.getEditMessageText(
                        callbackQuery,
                        "Выберите категорию",
                        keyboardFactory.getInlineKeyboardMarkup(
                                List.of("Зарплата", "Реклама", "Налоги", "Бытовые расходы", "Комиссия", "Назад"),
                                List.of(3, 2),
                                List.of(CATEGORY_SALARY, CATEGORY_ADS, CATEGORY_TAX, CATEGORY_EXPENSE, CATEGORY_COMMISSION, LOGIN)
                        )
                );

            case REPORT:
                return methodFactory.getEditMessageText(
                        callbackQuery,
                        "За какой период вы хотите получить отчет?",
                        keyboardFactory.getInlineKeyboardMarkup(
                                List.of("Сегодня", "За неделю", "За месяц", "Указать гггг-мм-дд", "Назад"),
                                List.of(3, 2),
                                List.of(TODAY, WEEK, MONTH, USER_DATE, LOGIN)
                        )
                );

            default:
                log.warn("Неизвестная callbackData: {}", callbackData);
                return null;
        }
    }

    private BotApiMethod<?> incomeMethod(CallbackQuery callbackQuery, Long chatId, UserSession session) {
        log.info("Обработка INCOME для chatId: {}", chatId);
        List<Client> clients;
        try {
            clients = webFluxBuilder.getClients(session.getUsername(), session.getJwt());
        } catch (Exception e) {
//            if (e.getMessage()== HttpStatus.)
            log.error("Ошибка при получении списка клиентов: {}", e.getMessage());
            return methodFactory.getSendMessage(chatId, "Произошла ошибка при получении списка клиентов. Попробуйте позже.", null);
        }

        if (clients.isEmpty()) {
            return methodFactory.getSendMessage(chatId, "У вас пока нет клиентов. Создайте нового клиента."
                    , keyboardFactory.getInlineKeyboardMarkup(List.of("Добавить клиента"), List.of(1), List.of("add_client")));
        }

        // Формирование списка клиентов для inline-клавиатуры
        List<String> clientNames = clients.stream()
                .map(Client::getFullName)
                .collect(Collectors.toList());
        clientNames.add("Добавить");

        log.info("Список имён: {}", clientNames);

        List<String> clientCallbacks = clients.stream()
                .map(client -> "client_" + client.getId()) // Генерируем уникальное callbackData для каждого клиента
                .collect(Collectors.toList());
        clientCallbacks.add(ADD_CLIENT_CONFIG);
        log.info("Список кнопок"+clientCallbacks);
        return methodFactory.getSendMessage(
                chatId,
                "Выберите клиента из списка или создайте нового:",
                keyboardFactory.getInlineKeyboardMarkup(
                        clientNames,
                        List.of(clientNames.size()),
                        clientCallbacks
                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        if (session.isAwaitingAmountMoney()) {
            Double amountMoney = Double.valueOf(message.getText());
            session.setAwaitingAmountMoney(false);
            session.setAmountMoney(amountMoney);
            session.setAwaitingCategory(true);
            userSessionManager.updateSession(chatId, session);
            //Здесь делает запрос post для указания прибыли:
            try {
                if (webFluxBuilder.incomeRequest(chatId, session.getJwt(), amountMoney)) {
                    return methodFactory.getSendMessage(chatId, "Успешно добавили прибыль.", null);
                } else {
                    return methodFactory.getSendMessage(chatId, "Произошла ошибка, попробуйте ещё раз", null);
                }
            } catch (Exception e) {
                log.info("Произошла ошибка при отправке: " + e.getMessage());
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(message.getChatId(), "Заглушка, тут будет отчет наверное (была команда /report)", null);
    }
}
