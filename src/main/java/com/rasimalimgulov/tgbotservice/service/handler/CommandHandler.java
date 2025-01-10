package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.Command.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CommandHandler {
    final KeyboardFactory keyboardFactory;

    public CommandHandler(KeyboardFactory keyboardFactory) {
        this.keyboardFactory = keyboardFactory;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        String command = message.getText();
        switch (command) {
            case START -> {
                return start(message);
            }
            case REPORT -> {
                return report(message);
            }
            case SETTINGS->{
                return settings(message);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
    return SendMessage.builder().chatId(message.getChatId()).text("Не поддерживаемая команда :(").build();
    }

    private BotApiMethod<?> settings(Message message) {
      return SendMessage.builder().chatId(message.getChatId()).text("Скоро мы добавим настройки. /n По идее должны появиться варианты настроек.").build();
    }

    private BotApiMethod<?> report(Message message) {
      return SendMessage.builder().chatId(message.getChatId()).text("У вас пока нет отчетов. ").disableWebPagePreview(true).build();
    }

    private BotApiMethod<?> start(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .replyMarkup(keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Мои отчёты","Настройки"),
                        List.of(2),
                        List.of("dadad","adfasfdaf")
                ))
                .text("""
                        Добро пожаловать в наш бот! 🎉😊
                        Здесь вы можете найти много полезной информации !Если у вас есть вопросы, просто напишите нам.
                        Мы всегда рады помочь ! 🤖✨
                      """)
                .build();
    }
}
