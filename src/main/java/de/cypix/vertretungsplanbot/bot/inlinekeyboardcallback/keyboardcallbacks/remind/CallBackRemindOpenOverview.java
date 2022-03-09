package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.remind;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallBackBuilder;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.util.HashMap;

public class CallBackRemindOpenOverview implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        for (String className : SQLManager.getAllNotifiesByChatId(chat.id())) {
            inlineKeyboard.addRow(new InlineKeyboardButton(className).callbackData(
                    new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "overviewReminds").addData("class", className).build()));
        }

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Bitte wähle eine Klasse:")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(inlineKeyboard);

        VertretungsPlanBot.getBot().execute(editMessageText);

        return true;
    }

    @Override
    public String getKey() {
        return "openOverview";
    }
}
