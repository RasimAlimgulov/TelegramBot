package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.authentication.AuthenticationManager;
import com.rasimalimgulov.tgbotservice.service.manager.client.ClientManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSession;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSessionManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MessageHandler {
    final AuthenticationManager authenticationManager;
    final ReportManager reportManager;
    final UserSessionManager sessionManager;
    final ClientManager clientManager;

    public MessageHandler(AuthenticationManager authenticationManager, ReportManager reportManager, UserSessionManager sessionManager, ClientManager clientManager) {
        this.authenticationManager = authenticationManager;
        this.reportManager = reportManager;
        this.sessionManager = sessionManager;
        this.clientManager = clientManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        UserSession session = sessionManager.getSession(message.getChatId());
        if (session.isAwaitingLogin() || session.isAwaitingPassword()) {
            return authenticationManager.answerMessage(message, bot);
        }
        if (session.isAwaitingNameNewClient() || session.isAwaitingPhoneNewClient() || session.isAwaitingServiceTypeNewClient()){
            return clientManager.answerMessage(message,bot);
        }

            if (session.isAwaitingAmountMoney()) {
                return reportManager.answerMessage(message, bot);
            }

        return null;
    }
}
