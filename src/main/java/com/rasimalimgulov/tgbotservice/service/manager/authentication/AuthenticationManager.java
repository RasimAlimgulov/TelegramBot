package com.rasimalimgulov.tgbotservice.service.manager.authentication;

import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.AbstractManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    public AuthenticationManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
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
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessions.getOrDefault(chatId, new UserSession());

        if (session.isAwaitingLogin()) {
            session.setLogin(message.getText());
            session.setAwaitingLogin(false);
            session.setAwaitingPassword(true);
            userSessions.put(chatId, session);
            return methodFactory.getSendMessage(chatId, "Введите пароль:", null);
        } else if (session.isAwaitingPassword()) {
            String login = session.getLogin();
            String password = message.getText();
            session.setAwaitingPassword(false);
            userSessions.put(chatId, session);
            return methodFactory.getSendMessage(chatId, String.format("Логин: %s\nПароль: %s\nДобро пожаловать!", login, password),
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Добавить доход","Добавить расход","Просмотреть отчёт","Настройки"),List.of(2,2),List.of(INCOME, OUTCOME,REPORT,SETTINGS)));
        }

        return methodFactory.getSendMessage(chatId, "Для начала авторизации нажмите кнопку 'Войти'.", null);
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    private BotApiMethod<?> startLoginProcess(Long chatId) {
        UserSession session = userSessions.getOrDefault(chatId, new UserSession());
        session.setAwaitingLogin(true);
        userSessions.put(chatId, session);
        return methodFactory.getSendMessage(chatId, "Введите логин:", null);
    }
}
