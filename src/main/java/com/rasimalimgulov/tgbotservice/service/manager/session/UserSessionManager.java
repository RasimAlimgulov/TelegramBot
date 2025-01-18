package com.rasimalimgulov.tgbotservice.service.manager.session;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSessionManager {

    final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    public UserSession getSession(Long chatId) {
        return userSessions.getOrDefault(chatId, new UserSession());
    }

    public void updateSession(Long chatId, UserSession session) {
        userSessions.put(chatId, session);
    }

    public void removeSession(Long chatId) {
        userSessions.remove(chatId);
    }
}
