package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.authentication.AuthenticationManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.settings.SettingsManager;
import com.rasimalimgulov.tgbotservice.service.manager.start.StartManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.rasimalimgulov.tgbotservice.service.data.Command.*;
import static jakarta.ws.rs.Priorities.AUTHENTICATION;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CommandHandler {
    final ReportManager reportManager;
    final SettingsManager settingsManager;
    final StartManager startManager;
    final AuthenticationManager authenticationManager;

    public CommandHandler(ReportManager reportManager, SettingsManager settingsManager, StartManager startManager, AuthenticationManager authenticationManager) {
        this.reportManager = reportManager;
        this.settingsManager = settingsManager;
        this.startManager = startManager;
        this.authenticationManager = authenticationManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        String command = message.getText();
        switch (command) {
            case START_COMMAND -> {
                System.out.println("Выполнился блок START_COMMAND в классе CommandHandler");
                return startManager.answerCommand(message, bot);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
        return SendMessage.builder().chatId(message.getChatId()).text("Для начала работы бота нажмите на /start").build();
    }
}
