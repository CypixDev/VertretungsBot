package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.remind;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

import java.util.HashMap;

public class CallBackRemindEnterRemind implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Bitte gebe die gewünschte Zeit ein.\n" +
                "Format: THH:mm oder HH:mm\n" +
                "Beispiel: T22:42\n" +
                "Das T steht für einen Tag vorher.")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(new InlineKeyboardMarkup());
        VertretungsPlanBot.getBot().execute(editMessageText);

        VertretungsPlanBot.getEnterMessageManager().startInput(chat.id(), data.get("class"));


        return true;
    }

    @Override
    public String getKey() {
        return "enterRemind";
    }


}
