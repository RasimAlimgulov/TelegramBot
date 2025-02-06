package com.rasimalimgulov.tgbotservice.service.manager.settings;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;
@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SettingsManager extends AbstractManager {
    final UserSessionManager userSessionManager;
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final WebFluxBuilder webFluxBuilder;
    public SettingsManager(UserSessionManager userSessionManager, AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, WebFluxBuilder webFluxBuilder) {
        this.userSessionManager = userSessionManager;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.webFluxBuilder = webFluxBuilder;
    }

    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(message.getChatId(),
                "Заглушка, тут будет меню настройки (была команда /settings)", null);
    }

    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        bot.execute(methodFactory.getAnswerCallbackQuery(callbackQuery));
        String callbackData=callbackQuery.getData();
        Long chatId=callbackQuery.getMessage().getChatId();
        UserSession session=userSessionManager.getSession(chatId);
        switch (callbackData) {
            case SETTINGS -> {
                return methodFactory.getEditMessageText(callbackQuery,
                        "Выберите действие:", keyboardFactory.getInlineKeyboardMarkup(
                                List.of("Изменить логин", "Изменить пароль", "Просмотреть права доступа","Выйти из аккаунта"), List.of(1,1,1,1), List.of(CHANGE_LOGIN,CHANGE_PASSWORD,CHECK_ROLES,LOG_OUT)));
            }
            case CHANGE_LOGIN -> {
                session.setAwaitingNewLogin(true);
                userSessionManager.updateSession(chatId,session);
                return methodFactory.getEditMessageText(callbackQuery,
                        "Введите новый логин", null);
            }
            case CHANGE_PASSWORD -> {
                session.setAwaitingNewPassword(true);
                userSessionManager.updateSession(chatId,session);
                return methodFactory.getEditMessageText(callbackQuery,
                        "Введите новый пароль", null);
            }
            case CHECK_ROLES -> {
                List<String> roles=null;
                try {
                   roles =webFluxBuilder.getRoles(session.getUsername(),session.getJwt());
                }
                catch (Exception e){
                    return methodFactory.getSendMessage(chatId, "Не получилось получить роли.",
                            keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));
                }
                return methodFactory.getSendMessage(chatId, "Ваши роли: "+roles,
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));
            }

        }
            return null;

    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId=message.getChatId();
        UserSession session=userSessionManager.getSession(chatId);
        if (session.isAwaitingNewLogin()){
            session.setAwaitingNewLogin(false);
            String oldLogin=session.getUsername();
            String newLogin=message.getText();
            try {
                log.info("Выполняется запрос на изменение логина");
                webFluxBuilder.changeLogin(oldLogin,newLogin,session.getJwt());
                session.setUsername(newLogin);
            }
            catch (Exception e){
             log.info(e);
                return methodFactory.getSendMessage(chatId, "Не получилось изменить логин",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));
            }
            userSessionManager.updateSession(chatId,session);
            return methodFactory.getSendMessage(chatId, "Успешно сменили логин",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));

            ///Здесь запрос на создание нового логина
        }
        if (session.isAwaitingNewPassword()){
            String password=message.getText();
            try {
                webFluxBuilder.changePassword(session.getUsername(),password,session.getJwt());
            }
            catch (Exception e){
                return methodFactory.getSendMessage(chatId, "Не получилось изменить пароль",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));
            }
            return methodFactory.getSendMessage(chatId, "Успешно сменили пароль",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"),List.of(1),List.of(MAIN_PAGE)));
            ///Здесь запрос на создание нового пароля
        }

        return null;
    }
}
