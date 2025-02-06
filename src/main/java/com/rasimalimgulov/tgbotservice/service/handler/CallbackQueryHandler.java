package com.rasimalimgulov.tgbotservice.service.handler;

import com.rasimalimgulov.tgbotservice.service.manager.authentication.AuthenticationManager;
import com.rasimalimgulov.tgbotservice.service.manager.client.ClientManager;
import com.rasimalimgulov.tgbotservice.service.manager.comment.CommentManager;
import com.rasimalimgulov.tgbotservice.service.manager.expencecategory.ExpenseCategoryManager;
import com.rasimalimgulov.tgbotservice.service.manager.money.MoneyManager;
import com.rasimalimgulov.tgbotservice.service.manager.period.PeriodManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.AllReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.ExpenseReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.IncomeReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.report.ReportManager;
import com.rasimalimgulov.tgbotservice.service.manager.servicetype.ServiceTypeManager;
import com.rasimalimgulov.tgbotservice.service.manager.session.LogOutManager;
import com.rasimalimgulov.tgbotservice.service.manager.settings.SettingsManager;
import com.rasimalimgulov.tgbotservice.service.manager.start.StartManager;
import com.rasimalimgulov.tgbotservice.service.manager.transaction.ExpenseTransactionManager;
import com.rasimalimgulov.tgbotservice.service.manager.transaction.IncomeTransactionManager;
import com.rasimalimgulov.tgbotservice.telegram.Bot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.rasimalimgulov.tgbotservice.service.data.CallbackData.*;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {
    final SettingsManager settingsManager;
    final ReportManager reportManager;
    final AuthenticationManager authenticationManager;
    final ClientManager clientManager;
    final ServiceTypeManager serviceTypeManager;
    final MoneyManager moneyManager;
    final IncomeTransactionManager incomeTransactionManager;
    final StartManager startManager;
    final ExpenseCategoryManager expenseCategoryManager;
    final CommentManager commentManager;
    final ExpenseTransactionManager expenseTransactionManager;
    final IncomeReportManager incomeReportManager;
    final ExpenseReportManager expenseReportManager;
    final AllReportManager allReportManager;
    final PeriodManager periodManager;
    final LogOutManager logOutManager;

    public CallbackQueryHandler(SettingsManager settingsManager, ReportManager reportManager, AuthenticationManager authenticationManager, ClientManager clientManager, ServiceTypeManager serviceTypeManager, MoneyManager moneyManager, IncomeTransactionManager incomeTransactionManager, StartManager startManager, ExpenseCategoryManager expenseCategoryManager, CommentManager commentManager, ExpenseTransactionManager expenseTransactionManager, IncomeReportManager incomeReportManager, ExpenseReportManager expenseReportManager, AllReportManager allReportManager, PeriodManager periodManager, LogOutManager logOutManager) {
        this.settingsManager = settingsManager;
        this.reportManager = reportManager;
        this.authenticationManager = authenticationManager;
        this.clientManager = clientManager;
        this.serviceTypeManager = serviceTypeManager;
        this.moneyManager = moneyManager;
        this.incomeTransactionManager = incomeTransactionManager;
        this.startManager = startManager;
        this.expenseCategoryManager = expenseCategoryManager;
        this.commentManager = commentManager;
        this.expenseTransactionManager = expenseTransactionManager;
        this.incomeReportManager = incomeReportManager;
        this.expenseReportManager = expenseReportManager;
        this.allReportManager = allReportManager;
        this.periodManager = periodManager;
        this.logOutManager = logOutManager;
    }

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        String callbackData = callbackQuery.getData();
        System.out.println("Выполняется метод answer класса CallBackQueryHandler");
        if (callbackData.contains("client_")) {
            return clientManager.answerCallbackQuery(callbackQuery, bot);
        }
        if (callbackData.contains("serviceType_")) {
            return serviceTypeManager.answerCallbackQuery(callbackQuery, bot);
        }
        if (callbackData.contains("status_")) {
            return incomeTransactionManager.answerCallbackQuery(callbackQuery, bot);
        }
        if (callbackData.contains("money_type")) {
            return moneyManager.answerCallbackQuery(callbackQuery, bot);
        }
        if (callbackData.contains("category_")) {
            return expenseCategoryManager.answerCallbackQuery(callbackQuery, bot);
        }
        if (callbackData.contains("period_") || callbackData.equals(DETAILS)) {
            return periodManager.answerCallbackQuery(callbackQuery, bot);
        }
        switch (callbackData) {
            case MAIN_PAGE -> {
                return startManager.answerCallbackQuery(callbackQuery, bot);
            }
            case LOGIN -> {
                return authenticationManager.answerCallbackQuery(callbackQuery, bot);
            }
            case REPORT, INCOME, OUTCOME -> {
                return reportManager.answerCallbackQuery(callbackQuery, bot);
            }
            case EXPENSE_REPORT, EXPENSE_REPORT_BY_CATEGORY -> {
                return expenseReportManager.answerCallbackQuery(callbackQuery, bot);
            }
            case INCOME_REPORT, INCOME_REPORT_BY_CATEGORY, INCOME_REPORT_BY_CLIENT -> {
                return incomeReportManager.answerCallbackQuery(callbackQuery, bot);
            }
            case ALL_REPORT, ALL_REPORT_TOTAL -> {
                return allReportManager.answerCallbackQuery(callbackQuery, bot);
            }
            case ADD_EXPENSE_CATEGORY -> {
                return expenseCategoryManager.answerCallbackQuery(callbackQuery, bot);
            }
            case ADD_CLIENT_CONFIG, ADD_CLIENT_REQUEST -> {
                return clientManager.answerCallbackQuery(callbackQuery, bot);
            }
            case ADD_TYPE_SERVICE -> {
                return serviceTypeManager.answerCallbackQuery(callbackQuery, bot);
            }
            case MONEY_COUNT -> {
                return moneyManager.answerCallbackQuery(callbackQuery, bot);
            }
            case ADD_COMMENT -> {
                return commentManager.answerCallbackQuery(callbackQuery, bot);
            }
            case TRANSACTION_INCOME_REQUEST -> {
                return incomeTransactionManager.answerCallbackQuery(callbackQuery, bot);
            }
            case TRANSACTION_OUTCOME_REQUEST -> {
                return expenseTransactionManager.answerCallbackQuery(callbackQuery, bot);
            }
            case CHANGE_LOGIN, CHECK_ROLES, CHANGE_PASSWORD, SETTINGS -> {
                return settingsManager.answerCallbackQuery(callbackQuery, bot);
            }
            case LOG_OUT -> {
                return logOutManager.answerCallbackQuery(callbackQuery, bot);
            }
        }
        return null;
    }
}
