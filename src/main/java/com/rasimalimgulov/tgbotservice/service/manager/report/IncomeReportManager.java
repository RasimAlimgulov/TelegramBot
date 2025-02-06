package com.rasimalimgulov.tgbotservice.service.manager.report;


import com.rasimalimgulov.tgbotservice.dto.Client;
import com.rasimalimgulov.tgbotservice.dto.ExpenseCategory;
import com.rasimalimgulov.tgbotservice.dto.TransactionType;
import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.AbstractManager;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSession;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSessionManager;
import com.rasimalimgulov.tgbotservice.service.webflux.SendExcelFile;
import com.rasimalimgulov.tgbotservice.service.webflux.WebFluxBuilder;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IncomeReportManager extends AbstractManager {
    final AnswerMethodFactory methodFactory;
    final KeyboardFactory keyboardFactory;
    final UserSessionManager userSessionManager;
    final WebFluxBuilder webFluxBuilder;
    final SendExcelFile sendExcelFile;
    public IncomeReportManager(AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory, UserSessionManager userSessionManager, WebFluxBuilder webFluxBuilder, SendExcelFile sendExcelFile) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userSessionManager = userSessionManager;
        this.webFluxBuilder = webFluxBuilder;
        this.sendExcelFile = sendExcelFile;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        bot.execute(methodFactory.getAnswerCallbackQuery(callbackQuery));
        String callbackData = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        UserSession session = userSessionManager.getSession(chatId);

        switch (callbackData) {
            case INCOME_REPORT: {
                session.setReportType(INCOME_REPORT);
                userSessionManager.updateSession(chatId,session);
                return methodFactory.getSendMessage(chatId, " Выберите период для отчета:", keyboardFactory.getInlineKeyboardMarkup(
                        List.of("За сегодня", "За неделю", "За месяц", "Произвольный период"),
                        List.of(2, 2),
                        List.of(DAY, WEEK, MONTH, USER_PERIOD)));
            }
            case INCOME_REPORT_BY_CLIENT: {
              return sendIncomeReportByClients(session,chatId,bot);
            }
            case INCOME_REPORT_BY_CATEGORY: {
                return sendIncomeReportByCategories(session,chatId,bot);
            }
            default:
                return null;
        }
    }
    public BotApiMethod<?> sendIncomeReportByClients(UserSession session, Long chatId,Bot bot) {
        byte[] fileData = null;
        try {
            fileData=webFluxBuilder.downloadIncomeReportByClients(session.getUsername(), session.getJwt());
        }catch (Exception e){
            return methodFactory.getSendMessage(chatId,"Произошла ошибка при запросе на получение отчёта",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Создать новый отчет","Главное меню"),List.of(1,1),List.of(REPORT,MAIN_PAGE)));
        }
        return sendExcelFile.sendFileToBot(chatId, fileData, bot,methodFactory,keyboardFactory);
    }

    public BotApiMethod sendIncomeReportByCategories(UserSession session, Long chatId, Bot bot) {
        byte[] fileData = null;
        try {
           fileData= webFluxBuilder.downloadIncomeReportByCategories(session.getUsername(), session.getJwt());
        }catch (Exception e){
            return methodFactory.getSendMessage(chatId,"Произошла ошибка при запросе на получение отчёта",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Создать новый отчет","Главное меню"),List.of(1,1),List.of(REPORT,MAIN_PAGE)));
        }
        return sendExcelFile.sendFileToBot(chatId, fileData, bot,methodFactory,keyboardFactory);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(message.getChatId(), "Заглушка, тут будет отчет наверное (была команда /report)", null);
    }
}
