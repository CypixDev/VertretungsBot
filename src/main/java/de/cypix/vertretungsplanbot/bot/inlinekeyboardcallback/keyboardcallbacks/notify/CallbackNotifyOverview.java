package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.notify;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.BaseResponse;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallBackBuilder;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

import java.util.HashMap;

public class CallbackNotifyOverview implements KeyboardCallback {


    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.addRow(
                new InlineKeyboardButton("Zurück").callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "openOverview").build()),
                new InlineKeyboardButton("Löschen")
                .callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "delete")
                        .addData("class", data.get("class"))
                        .build()));
        inlineKeyboard.addRow(new InlineKeyboardButton("Erinnerungen").callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "overviewReminds")
                .addData("class", data.get("class")).build())
                , new InlineKeyboardButton("Statistiken").callbackData("__"));

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Übersicht für "+data.get("class"))
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(inlineKeyboard);

        BaseResponse response = VertretungsPlanBot.getBot().execute(editMessageText);

        return true;
    }

    @Override
    public String getKey() {
        return "overview";
    }
}
