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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        bot.execute(methodFactory.getAnswerCallbackQuery(callbackQuery));
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
    public BotApiMethod<?> answerMessage(Message message, Bot bot) throws TelegramApiException { // реакция на пришедшее сообщение.
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        // Получаем сессию из менеджера сессий, если нет то вернёт новую сессию
        if (session.isAwaitingLogin()) {  // Проверяем, ожидается ли ввод логина
            session.setUsername(message.getText());
            session.setAwaitingLogin(false);
            session.setAwaitingPassword(true);//Ожидаем ввод пароля
            userSessionManager.updateSession(chatId, session);
            return methodFactory.getSendMessage(chatId, "Введите пароль:", null);
        } else if (session.isAwaitingPassword()) {
            // Если ожидается ввод пароля получаем логин из сессии и пароль из пришедшего сообщения
            String login = session.getUsername();
            String password = message.getText();
            try {
                // Попытка аутентификации
                ResponseEntity<String> jwtResponse = webClient.authenticateRequest(login, password);
                session.setAwaitingPassword(false);
                userSessionManager.updateSession(chatId, session);
                // Если исключений нет, значит аутентификация прошла успешно
                String responseBody = jwtResponse.getBody();
                session.setJwt(responseBody); // Сохраняем JWT в сессию
                userSessionManager.updateSession(chatId, session); // Обновляем сессию

                // Возвращаем успешное сообщение
                return methodFactory.getSendMessage(chatId, String.format("%s вы успешно вошли!", login),
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"), List.of(1), List.of(MAIN_PAGE)));

            } catch (WebClientResponseException.Unauthorized e) {
                session.setAwaitingPassword(false);
                userSessionManager.updateSession(chatId, session);
                return methodFactory.getSendMessage(chatId, "Не верный логин или пароль.",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Попробовать ещё раз"), List.of(1), List.of(LOGIN)));
            } catch (Exception e) {
                session.setAwaitingPassword(false);
                userSessionManager.updateSession(chatId, session);
                // Обработка других ошибок
                log.info("Произошла ошибка: " + e.getMessage());
                return methodFactory.getSendMessage(chatId, "Ошибка при запросе. Попробуйте позже.",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Попробовать ещё раз"), List.of(1), List.of(LOGIN)));
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
