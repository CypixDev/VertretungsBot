package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import java.util.HashMap;

public interface KeyboardCallback {

    boolean handleCallBack(String key, User user, Chat chat, HashMap<String, String> data);

    String getKey();
}
