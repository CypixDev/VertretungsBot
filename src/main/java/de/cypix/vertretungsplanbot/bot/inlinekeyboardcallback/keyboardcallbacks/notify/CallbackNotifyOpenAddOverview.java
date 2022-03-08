package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.notify;

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

public class CallbackNotifyOpenAddOverview implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;

        String[] classes = {"AA", "AB", "AD", "AE", "AF", "AG", "AI", "AK", "AM", "AP", "AV", "AW", "AZ",
                "B1", "B2", "CE", "CI", "CM", "DI", "DW", "EE", "EM"};


        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

        for (int i = 0; i + 4 < classes.length; i += 4) {
            inlineKeyboard.addRow(new InlineKeyboardButton(classes[i]).callbackData(
                            new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[i]).build()),
                    new InlineKeyboardButton(classes[i + 1]).callbackData(
                            new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[i + 1]).build()),
                    new InlineKeyboardButton(classes[i + 2]).callbackData(
                            new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[i + 2]).build()),
                    new InlineKeyboardButton(classes[i + 3]).callbackData(
                            new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[i + 3]).build()));

        }
        inlineKeyboard.addRow(
                new InlineKeyboardButton(classes[classes.length - 2]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[classes.length - 2]).build()),
                new InlineKeyboardButton(classes[classes.length - 1]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[classes.length - 1]).build()));


        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Hier die Liste deiner Notifications:")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(inlineKeyboard);

        VertretungsPlanBot.getBot().execute(editMessageText);



        return getKey().equalsIgnoreCase(key);
    }

    @Override
    public String getKey() {
        return "openAddOverview";
    }
}
