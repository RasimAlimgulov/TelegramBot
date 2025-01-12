package com.rasimalimgulov.tgbotservice.service;

import com.rasimalimgulov.tgbotservice.service.handler.CallbackQueryHandler;
import com.rasimalimgulov.tgbotservice.service.handler.CommandHandler;
import com.rasimalimgulov.tgbotservice.service.handler.MessageHandler;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UpdateDispatcher {
    final CallbackQueryHandler callbackQueryHandler;
    final CommandHandler commandHandler;
    final MessageHandler messageHandler;

    public UpdateDispatcher(CallbackQueryHandler callbackQueryHandler, CommandHandler commandHandler, MessageHandler messageHandler) {
        this.callbackQueryHandler = callbackQueryHandler;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;
    }

    public BotApiMethod<?> distribute(Update update, Bot bot){
        System.out.println("Выполняется метод разделения на команды,колбеки,сообщения в классе UpdateDispatcher");
        if (update.hasCallbackQuery()){
            return callbackQueryHandler.answer(update.getCallbackQuery(), bot);
        }
        if (update.hasMessage()){
            Message message=update.getMessage();
            if (message.hasText()){
                if (message.getText().charAt(0)=='/'){
                    return commandHandler.answer(message, bot);
                }
            }
            return messageHandler.answer(message, bot);
        }
        System.out.println("Unsupported update: "+update);
        return null;
    }
}
