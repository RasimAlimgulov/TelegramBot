package com.rasimalimgulov.tgbotservice.service.manager;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ReportManager {

    public BotApiMethod<?> reportCommand(Message message) {
        return SendMessage.builder().chatId(message.getChatId()).text("У вас пока нет отчетов. ").disableWebPagePreview(true).build();
    }

    public BotApiMethod<?> reportCallbackQuery(CallbackQuery callbackQuery) {
        return EditMessageText.builder().chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text("У вас пока нет отчетов. ")
                .disableWebPagePreview(true).build();
    }

}
