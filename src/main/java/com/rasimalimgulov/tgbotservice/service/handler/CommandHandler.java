package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.SettingsManager;
import com.rasimalimgulov.tgbotservice.service.manager.StartManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import static com.rasimalimgulov.tgbotservice.service.data.Command.*;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CommandHandler {
    final ReportManager reportManager;
    final SettingsManager settingsManager;
    final StartManager startManager;

    public CommandHandler(ReportManager reportManager, SettingsManager settingsManager, StartManager startManager) {
        this.reportManager = reportManager;
        this.settingsManager = settingsManager;
        this.startManager = startManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        String command = message.getText();
        switch (command) {
            case START_COMMAND -> {
                return startManager.startCommand(message);
            }
            case REPORT_COMMAND -> {
                return reportManager.reportCommand(message);
            }
            case SETTINGS_COMMAND ->{
                return settingsManager.settingsCommand(message);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
    return SendMessage.builder().chatId(message.getChatId()).text("Не поддерживаемая команда :(").build();
    }
}
