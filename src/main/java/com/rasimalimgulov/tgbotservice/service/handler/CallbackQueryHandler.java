package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.authentication.AuthenticationManager;
import com.rasimalimgulov.tgbotservice.service.manager.client.ClientManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.settings.SettingsManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {
    final SettingsManager settingsManager;
    final ReportManager reportManager;
    final AuthenticationManager authenticationManager;
    final ClientManager clientManager;

    public CallbackQueryHandler(SettingsManager settingsManager, ReportManager reportManager, AuthenticationManager authenticationManager, ClientManager clientManager) {
        this.settingsManager = settingsManager;
        this.reportManager = reportManager;
        this.authenticationManager = authenticationManager;
        this.clientManager = clientManager;
    }

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        System.out.println("Выполняется метод answer класса CallBackQueryHandler");
        switch (callbackData) {
            case LOGIN -> {
                return authenticationManager.answerCallbackQuery(callbackQuery, bot);
            }
            case REPORT, INCOME, OUTCOME -> {
                return reportManager.answerCallbackQuery(callbackQuery, bot);
            }
            case ADD_CLIENT -> {
                return clientManager.answerCallbackQuery(callbackQuery, bot);
            }
            case SETTINGS -> {
                return settingsManager.answerCallbackQuery(callbackQuery, bot);
            }
        }
        return null;
    }
}
