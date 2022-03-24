package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.cleverbot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.util.HashMap;

public class CallbackCleverBotAgree implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(data.get("decision").equals("yes")){
            SQLManager.agreeCleverBot(chat.id());
            EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Die Unterhaltung wird nun gestartet....")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(new InlineKeyboardMarkup());
            VertretungsPlanBot.getBot().execute(editMessageText);
        }else if(data.get("decision").equals("no")){
            EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Erfolgreich abgelehnt!")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(new InlineKeyboardMarkup());
            VertretungsPlanBot.getBot().execute(editMessageText);

        }

        return true;
    }

    @Override
    public String getKey() {
        return "agree";
    }
}
