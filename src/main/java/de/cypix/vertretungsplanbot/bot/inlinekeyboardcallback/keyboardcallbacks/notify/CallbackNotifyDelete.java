package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.notify;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.util.HashMap;

public class CallbackNotifyDelete implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        SQLManager.deleteNotification(chat.id(), data.get("class"));

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(),
                "Erfolgreich Notifications für "+data.get("class")+" gelöscht!")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(new InlineKeyboardMarkup());

        VertretungsPlanBot.getBot().execute(editMessageText);
        return true;
    }

    @Override
    public String getKey() {
        return "delete";
    }
}
