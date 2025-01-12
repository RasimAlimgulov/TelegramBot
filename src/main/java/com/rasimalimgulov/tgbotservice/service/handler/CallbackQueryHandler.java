package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.authentication.AuthenticationManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.settings.SettingsManager;
import com.rasimalimgulov.tgbotservice.service.manager.transaction.MyTransactionManager;
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
    final MyTransactionManager transactionManager;
    final AuthenticationManager authenticationManager;

    public CallbackQueryHandler(SettingsManager settingsManager, ReportManager reportManager, MyTransactionManager transactionManager, AuthenticationManager authenticationManager) {
        this.settingsManager = settingsManager;
        this.reportManager = reportManager;
        this.transactionManager = transactionManager;
        this.authenticationManager = authenticationManager;
    }

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        System.out.println("Выполняется метод answer класса CallBackQueryHandler");
        switch (callbackData) {
            case REPORT -> {
                System.out.println("Выполнился switch(REPORT) в классе CallBackQueryHandler");
                return reportManager.answerCallbackQuery(callbackQuery,bot);
            }
            case SETTINGS -> {
                System.out.println("Выполнился switch(SETTINGS) в классе CallBackQueryHandler");
                return settingsManager.answerCallbackQuery(callbackQuery,bot);
            }

            case INCOME, OUTCOME, TRANSACTION-> {System.out.println("Выполнился switch(TRANSACTION,INCOME,OUTCOME) в классе CallBackQueryHandler");
                                          return transactionManager.answerCallbackQuery(callbackQuery,bot);}
            case LOGIN,PASSWORD -> { System.out.println("Выполнился switch(LOGIN,PASSWORD) в классе CallBackQueryHandler");
                return authenticationManager.answerCallbackQuery(callbackQuery,bot);
            }
        }
        return null;
    }
}
