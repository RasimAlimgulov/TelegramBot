package com.rasimalimgulov.tgbotservice.service.webflux;

import com.rasimalimgulov.tgbotservice.service.factory.AnswerMethodFactory;
import com.rasimalimgulov.tgbotservice.service.factory.KeyboardFactory;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.List;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.MAIN_PAGE;
import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.REPORT;

@Log4j2
@Component
public class SendExcelFile {
    public SendDocument createExcelDocument(Long chatId, byte[] fileData, String caption) {
        InputFile inputFile = new InputFile(new ByteArrayInputStream(fileData), "report.xlsx");
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId.toString());
        sendDocument.setDocument(inputFile);
        sendDocument.setCaption(caption);
        return sendDocument;
    }
    public BotApiMethod<?> sendFileToBot(Long chatId, byte[]fileData, Bot bot, AnswerMethodFactory methodFactory, KeyboardFactory keyboardFactory){
        SendDocument document = createExcelDocument(chatId, fileData, "Общий отчёт");
        try {
            bot.execute(document); // Отправляем файл
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке документа: ", e);
            return methodFactory.getSendMessage(chatId,"Не удалось получить файл",
                    keyboardFactory.getInlineKeyboardMarkup(List.of("Создать новый отчет","Главное меню"),List.of(1,1),List.of(REPORT,MAIN_PAGE)));
        }
        return methodFactory.getSendMessage(chatId,"Файл успешно получен",
                keyboardFactory.getInlineKeyboardMarkup(List.of("Создать новый отчет","Главное меню"),List.of(1,1),List.of(REPORT,MAIN_PAGE)));

    }
}
