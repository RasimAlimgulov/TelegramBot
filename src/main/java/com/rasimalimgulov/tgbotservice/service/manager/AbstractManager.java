package com.rasimalimgulov.tgbotservice.service.manager;

import com.rasimalimgulov.tgbotservice.telegram.Bot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class AbstractManager {
    public abstract BotApiMethod<?> answerCommand(Message message, Bot bot);
    public abstract BotApiMethod<?> answerMessage(Message message, Bot bot) throws TelegramApiException;
    public abstract BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException;

}
