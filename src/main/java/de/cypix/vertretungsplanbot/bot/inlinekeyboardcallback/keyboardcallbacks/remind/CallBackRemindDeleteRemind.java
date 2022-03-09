package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.remind;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.util.HashMap;

public class CallBackRemindDeleteRemind implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        String hour = data.get("hour");
        String className = data.get("class");

        if(SQLManager.deleteRemind(chat.id(), className, hour)){
            EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(),
                    "Erfolgreich Erinnerung für "+className+" um "+hour+" gelöscht!")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(new InlineKeyboardMarkup());

            VertretungsPlanBot.getBot().execute(editMessageText);
        }else{
            EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(),
                    "Ein Fehler ist aufgetreten!")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(new InlineKeyboardMarkup());

            VertretungsPlanBot.getBot().execute(editMessageText);
        }

        return true;
    }

    @Override
    public String getKey() {
        return "deleteRemind";
    }
}
