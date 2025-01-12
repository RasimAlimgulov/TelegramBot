package com.rasimalimgulov.tgbotservice.service.manager.transaction;

import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.service.manager.AbstractManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MyTransactionManager extends AbstractManager {
    final AnswerMethodFactory answerMethodFactory;
    final KeyboardFactory keyboardFactory;

    public MyTransactionManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String callbackData = callbackQuery.getData();
        switch (callbackData) {
            case INCOME -> incomeSettings(callbackQuery);
            case OUTCOME -> outcomeSettings(callbackQuery);
        }
        return null;
        }

    private BotApiMethod<?> outcomeSettings(CallbackQuery callbackQuery){
        return answerMethodFactory.getEditMessageText(callbackQuery,"Вы выбрали расход. Выберите категорию..."
                , keyboardFactory.getInlineKeyboardMarkup(List.of("Реклама","Налог","Разное","Назад")
                        ,List.of(2,2),List.of(OUTCOME_AD,OUTCOME_TAX,OUTCOME_ANOTHER,)));
    }

    private BotApiMethod<?> incomeSettings(CallbackQuery callbackQuery){
        return answerMethodFactory.getEditMessageText(callbackQuery,"Вы выбрали доход . Укажите сумму."
                , null);
    }

//    private BotApiMethod<?> chooseType(CallbackQuery callbackQuery){
//        return answerMethodFactory.getEditMessageText(callbackQuery,"Здесь вы можете выбрать тип транзакции транзакции."
//                , keyboardFactory.getInlineKeyboardMarkup(List.of("Доход","Расход"),List.of(2),List.of(INCOME, OUTCOME)));
//    }
}
