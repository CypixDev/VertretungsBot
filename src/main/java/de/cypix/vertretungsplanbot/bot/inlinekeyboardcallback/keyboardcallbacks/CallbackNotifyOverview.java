package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

import java.util.HashMap;

public class CallbackNotifyOverview implements KeyboardCallback {


    @Override
    public boolean handleCallBack(String key, User user, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.addRow(new InlineKeyboardButton("Behalten").callbackData("__"), new InlineKeyboardButton("Löschen").callbackData("__"));
        inlineKeyboard.addRow(new InlineKeyboardButton("Statistiken").callbackData("__"));

        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Hier die Liste deiner Notifications:").replyMarkup(inlineKeyboard));

        return true;
    }

    @Override
    public String getKey() {
        return "overview";
    }
}
