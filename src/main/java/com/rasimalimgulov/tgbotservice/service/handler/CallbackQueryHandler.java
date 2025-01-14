package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.authentication.AuthenticationManager;
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

    public CallbackQueryHandler(SettingsManager settingsManager, ReportManager reportManager, AuthenticationManager authenticationManager) {
        this.settingsManager = settingsManager;
        this.reportManager = reportManager;
        this.authenticationManager = authenticationManager;
    }

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        System.out.println("Выполняется метод answer класса CallBackQueryHandler");
        switch (callbackData) {
            case REPORT, INCOME, OUTCOME -> {
                System.out.println("выбрал Report,Income,Outcome в классе CallbackQueryHandler");
                return reportManager.answerCallbackQuery(callbackQuery,bot);
            }
            case SETTINGS -> {
                return settingsManager.answerCallbackQuery(callbackQuery,bot);
            }
            case LOGIN -> { System.out.println("Выполнился switch(LOGIN) в классе CallBackQueryHandler");
                return authenticationManager.answerCallbackQuery(callbackQuery,bot);
            }

        }
        return null;
    }
}
