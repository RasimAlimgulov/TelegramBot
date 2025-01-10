package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.SettingsManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;
import static com.rasimalimgulov.tgbotservice.service.data.Command.*;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CommandHandler {
    final KeyboardFactory keyboardFactory;
    final ReportManager reportManager;
    final SettingsManager settingsManager;

    public CommandHandler(KeyboardFactory keyboardFactory, ReportManager reportManager, SettingsManager settingsManager) {
        this.keyboardFactory = keyboardFactory;
        this.reportManager = reportManager;
        this.settingsManager = settingsManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        String command = message.getText();
        switch (command) {
            case START_COMMAND -> {
                return start(message);
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

    private BotApiMethod<?> start(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .replyMarkup(keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Мои отчёты","Настройки"),
                        List.of(2),
                        List.of(REPORT,SETTINGS)
                ))
                .text("""
                        Добро пожаловать в наш бот! 🎉😊
                        Здесь вы можете найти много полезной информации !Если у вас есть вопросы, просто напишите нам.
                        Мы всегда рады помочь ! 🤖✨
                      """)
                .build();
    }
}
