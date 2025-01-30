package com.rasimalimgulov.tgbotservice.service.manager.authentication;

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
public class AuthenticationManager extends AbstractManager {
    final WebFluxBuilder webClient;
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;

    public AuthenticationManager(WebFluxBuilder webClient, AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager) {
        this.webClient = webClient;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userSessionManager = userSessionManager;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        switch (callbackData) {
            case LOGIN -> {
                return startLoginProcess(chatId);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) { // реакция на пришедшее сообщение.
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);

        // Получаем сессию из менеджера сессий, если нет то вернёт новую сессию
        if (session.isAwaitingLogin()) {  // Проверяем, ожидается ли ввод логина
            session.setUsername(message.getText());
            session.setAwaitingLogin(false);
            session.setAwaitingPassword(true); //Ожидаем ввод пароля
            userSessionManager.updateSession(chatId, session);
            return methodFactory.getSendMessage(chatId, "Введите пароль:", null);
        } else if (session.isAwaitingPassword()) { // Если ожидается ввод пароля получаем логин из сессии и пароль из пришедшего сообщения
            String login = session.getUsername();
            String password = message.getText();
            String jwt = null;
            try {
                jwt = webClient.authenticateRequest(login, password);  // Отправляем запрос на API для аутентификации и получаем jwt при успешном аутен...
                log.info("Получен jwt токен: " + jwt);
            } catch (Exception e) {
                log.info("Ошибка при отправке запроса: " + e.getMessage());
            }
            if (jwt != null) {
                session.setJwt(jwt);
                session.setAwaitingPassword(false);
                userSessionManager.updateSession(chatId, session); // Сохраняем jwt в сессию.
                return methodFactory.getSendMessage(chatId, String.format("Добро пожаловать %s!", login),
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Добавить доход", "Добавить расход", "Просмотреть отчёт", "Настройки")
                                , List.of(2, 2), List.of(INCOME, OUTCOME, REPORT, SETTINGS)));
            } else {
                return methodFactory.getSendMessage(chatId, "Не верный логин или пароль!", null);
            }

        }

        return methodFactory.getSendMessage(chatId, "Для начала авторизации нажмите кнопку 'Войти'.", null);
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    private BotApiMethod<?> startLoginProcess(Long chatId) {
        UserSession session = userSessionManager.getSession(chatId);
        session.setAwaitingLogin(true);
        userSessionManager.updateSession(chatId, session);
        return methodFactory.getSendMessage(chatId, "Введите логин:", null);
    }

}
