package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.delete_all;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.util.HashMap;

public class CallbackDeleteAllConfirmation implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(data.get("decision").equalsIgnoreCase("yes")){
            SQLManager.deleteEverything(chat.id());
            EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Es wurden erfolgreich alle personenbezogenen Daten gelöscht!")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(new InlineKeyboardMarkup());

            VertretungsPlanBot.getBot().execute(editMessageText);
        }else{
            EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Der Vorgang wurde abgebrochen.")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(new InlineKeyboardMarkup());

            VertretungsPlanBot.getBot().execute(editMessageText);
        }


        return true;
    }

    @Override
    public String getKey() {
        return "confirm";
    }
}
