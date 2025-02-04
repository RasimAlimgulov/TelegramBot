package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.authentication.AuthenticationManager;
import com.rasimalimgulov.tgbotservice.service.manager.client.ClientManager;
import com.rasimalimgulov.tgbotservice.service.manager.comment.CommentManager;
import com.rasimalimgulov.tgbotservice.service.manager.expencecategory.ExpenseCategoryManager;
import com.rasimalimgulov.tgbotservice.service.manager.money.MoneyManager;
import com.rasimalimgulov.tgbotservice.service.manager.period.PeriodManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.servicetype.ServiceTypeManager;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSession;
import com.rasimalimgulov.tgbotservice.service.manager.session.UserSessionManager;
import com.rasimalimgulov.tgbotservice.service.manager.transaction.IncomeTransactionManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class MessageHandler {
    final AuthenticationManager authenticationManager;
    final ReportManager reportManager;
    final UserSessionManager sessionManager;
    final ClientManager clientManager;
    final ServiceTypeManager serviceTypeManager;
    final MoneyManager moneyManager;
    final IncomeTransactionManager incomeTransactionManager;
    final ExpenseCategoryManager expenseCategoryManager;
    final CommentManager commentManager;
    final PeriodManager periodManager;
    public MessageHandler(AuthenticationManager authenticationManager, ReportManager reportManager, UserSessionManager sessionManager, ClientManager clientManager, ServiceTypeManager serviceTypeManager, MoneyManager moneyManager, IncomeTransactionManager incomeTransactionManager, ExpenseCategoryManager expenseCategoryManager, CommentManager commentManager, PeriodManager periodManager) {
        this.authenticationManager = authenticationManager;
        this.reportManager = reportManager;
        this.sessionManager = sessionManager;
        this.clientManager = clientManager;
        this.serviceTypeManager = serviceTypeManager;
        this.moneyManager = moneyManager;
        this.incomeTransactionManager = incomeTransactionManager;
        this.expenseCategoryManager = expenseCategoryManager;
        this.commentManager = commentManager;
        this.periodManager = periodManager;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        UserSession session = sessionManager.getSession(message.getChatId());
        if (session.isAwaitingLogin() || session.isAwaitingPassword()) {
            return authenticationManager.answerMessage(message, bot);
        }
        if (session.isAwaitingNameNewClient() || session.isAwaitingPhoneNewClient()) {
            return clientManager.answerMessage(message, bot);
        }
        if (session.isAwaitingNewServiceType()) {
            return serviceTypeManager.answerMessage(message, bot);
        }
        if (session.isAwaitingAmountMoney()) {
            return moneyManager.answerMessage(message, bot);
        }
        if (session.isAwaitingComment()){
            return commentManager.answerMessage(message, bot);
        }
        if (session.getAwaitingExpenseCategory()){
            return expenseCategoryManager.answerMessage(message, bot);
        }
        if (session.isAwaitingCustomPeriod()){
            return periodManager.answerMessage(message, bot);
        }
        return null;
    }
}
