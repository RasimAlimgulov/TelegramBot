package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.SettingsManager;
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

    public CallbackQueryHandler(SettingsManager settingsManager, ReportManager reportManager) {
        this.settingsManager = settingsManager;
        this.reportManager = reportManager;
    }

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        switch (callbackData) {
            case REPORT -> {
                return reportManager.reportCallbackQuery(callbackQuery);
            }
            case SETTINGS -> {
                return settingsManager.settingsCallbackQuery(callbackQuery);
            }
        }
        return null;
    }
}
