package com.rasimalimgulov.tgbotservice.service.manager.session;

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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.LOGIN;
import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.LOG_OUT;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogOutManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager sessionManager;
    public LogOutManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserSessionManager sessionManager) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.sessionManager = sessionManager;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        Long chatId = callbackQuery.getMessage().getChatId();
        UserSession session=sessionManager.getSession(chatId);
        String callbackData = callbackQuery.getData();
        switch (callbackData){
            case LOG_OUT:{
                session.setUsername(null);
                session.setJwt(null);
                sessionManager.updateSession(chatId,session);
                return methodFactory.getSendMessage(chatId,"Вы успешно вышли из своего аккаунта.",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Войти"),List.of(1),List.of(LOGIN)));
            }
        }
        return null;
    }
    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) throws TelegramApiException {
        return null;
    }
}
