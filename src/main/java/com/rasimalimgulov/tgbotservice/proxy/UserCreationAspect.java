package com.rasimalimgulov.tgbotservice.proxy;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

@Aspect
@Component
public class UserCreationAspect {
//    @Pointcut("execution(* com.rasimalimgulov.tgbotservice.service.UpdateDispatcher.distribute(..))")
//    public void distributeMethodPointCut() {
//    }
//    @Around("distributeMethodPointCut()")
//    public Object distributeMethodAround(ProceedingJoinPoint joinPoint) throws Throwable {
//         Object[] args = joinPoint.getArgs();
//        Update update= (Update) args[0];
//
//        User telegramUser= null;
//        if (update.hasMessage()) {
//            telegramUser=update.getMessage().getFrom();
//        }else if (update.hasCallbackQuery()){
//            telegramUser=update.getCallbackQuery().getFrom();
//        }else {
//            return joinPoint.proceed();
//        }
//
//
//    }
}
