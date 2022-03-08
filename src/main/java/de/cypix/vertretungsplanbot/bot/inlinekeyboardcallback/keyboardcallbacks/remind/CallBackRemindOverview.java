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

public class CallBackRemindOverview implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;

        String className = data.get("class");
        long chatId = Long.parseLong(data.get("chatId"));

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

        SQLManager.getAllReminderByClassAndChatId(className, chatId);
        /*
          inlineKeyboard.addRow(new InlineKeyboardButton(className).callbackData(
          new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "overview").addData("class", className).build()));
         */

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Hier die Liste deiner Notifications:")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(inlineKeyboard);

        VertretungsPlanBot.getBot().execute(editMessageText);

        return true;
    }

    @Override
    public String getKey() {
        return "overview";
    }
}
