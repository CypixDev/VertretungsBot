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

import java.util.HashMap;

public class CallBackRemindOpenAddRemind implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Erinnerung hinzufügen:\n" +
                "Das T heißt einen Tag vorher. Also T22:00 bedeutet das man am Tag davor um 22:00 Erinnert wird.")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(getKeyBoard(data.get("class")));

        VertretungsPlanBot.getBot().execute(editMessageText);

        return true;
    }

    @Override
    public String getKey() {
        return "openAddRemind";
    }

    public static InlineKeyboardMarkup getKeyBoard(String className){
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();


        inlineKeyboard.addRow(
                new InlineKeyboardButton("06:00").callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "addRemind")
                        .addData("class", className)
                        .addData("hour", "06:00")
                        .build()),
                new InlineKeyboardButton("07:00").callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "addRemind")
                        .addData("class", className)
                        .addData("hour", "07:00")
                        .build()));

        inlineKeyboard.addRow(
                new InlineKeyboardButton("T20:00").callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "addRemind")
                        .addData("class", className)
                        .addData("hour", "T20:00")
                        .build()),
                new InlineKeyboardButton("T22:00").callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "addRemind")
                        .addData("class", className)
                        .addData("hour", "T22:00")
                        .build()));

        inlineKeyboard.addRow(new InlineKeyboardButton(("Zurück")).callbackData(
                new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "openOverviewReminds")
                        .addData("class", className)
                        .build()),
                new InlineKeyboardButton("Selber eingeben").callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "enterRemind")
                .addData("class", className)
                .build()));
        return inlineKeyboard;
    }
}
