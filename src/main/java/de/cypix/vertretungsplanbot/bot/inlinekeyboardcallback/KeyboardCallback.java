package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;

import java.util.HashMap;

public interface KeyboardCallback {

    boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data);

    String getKey();
}
