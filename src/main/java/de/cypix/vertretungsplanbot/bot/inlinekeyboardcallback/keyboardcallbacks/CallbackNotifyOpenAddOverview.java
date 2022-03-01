package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;

import java.util.HashMap;

public class CallbackNotifyOpenAddOverview implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        return getKey().equalsIgnoreCase(key);
    }

    @Override
    public String getKey() {
        return "openAddOverview";
    }
}
