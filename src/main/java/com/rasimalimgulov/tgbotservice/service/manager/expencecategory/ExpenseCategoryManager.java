package com.rasimalimgulov.tgbotservice.service.manager.expencecategory;

import com.rasimalimgulov.tgbotservice.dto.ExpenseCategory;
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

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseCategoryManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;

    public ExpenseCategoryManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder) {
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
        if (callbackData.contains("category_")) {
            String category = callbackData.split("category_")[1];
            session.setAwaitingExpenseCategory(false);
            session.setExpenseCategory(category);
            userSessionManager.updateSession(chatId, session);
            return answerMethodFactory.getSendMessage(chatId, "Сохранили категорию: " + category,
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Продолжить"), List.of(1), List.of(MONEY_COUNT)));
        }
        switch (callbackData) {
            case ADD_EXPENSE_CATEGORY -> {
                session.setAwaitingExpenseCategory(true);
                userSessionManager.updateSession(chatId, session);
                return answerMethodFactory.getSendMessage(chatId, "Введите название новой категории расходов", null);
            }
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        Long chatId = message.getChatId();
        UserSession session = userSessionManager.getSession(chatId);
        if (session.getAwaitingExpenseCategory()) {
            ExpenseCategory expenseCategory = null;
            try {
                expenseCategory = webFluxBuilder.addNewExpenseCategory(message.getText(), session.getUsername(), session.getJwt());
                session.setAwaitingExpenseCategory(false);
                session.setExpenseCategory(message.getText());
                userSessionManager.updateSession(chatId, session);
            } catch (WebClientResponseException.Unauthorized e) {
                return answerMethodFactory.getSendMessage(chatId, "У вас закончилась сессия. Чтобы продолжить работу войдите в свой аккаунт.",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Войти"), List.of(1), List.of(LOGIN)));
            } catch (WebClientResponseException.BadRequest e) {
                log.info(e.getMessage());
                return answerMethodFactory.getSendMessage(chatId, "Категория расходов уже существует." +
                                " Отправьте новое название или выберите существующую \uD83D\uDC47."
                        , keyboardFactory.getInlineKeyboardMarkup(
                                List.of(message.getText(), "Главное меню"), List.of(2), List.of("category_" + message.getText(), MAIN_PAGE)));
            } catch (Exception e) {
                log.error(e.getMessage());
                return answerMethodFactory.getSendMessage(chatId, "Произошла ошибка при добавлении категории расходов.",
                        keyboardFactory.getInlineKeyboardMarkup(List.of("Главное меню"), List.of(1), List.of(MAIN_PAGE)));
            }
            return answerMethodFactory.getSendMessage(chatId, "Успешно добавили новую категорию",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Выбрать категорию"), List.of(1), List.of(OUTCOME)));
        }

        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

}
