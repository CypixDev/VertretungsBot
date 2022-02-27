package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks;

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

public class CallbackNotifyAddClass implements KeyboardCallback {

    private static HashMap<String, String[]> classes;

    public CallbackNotifyAddClass() {
        classes = new HashMap<>();
        classes.put("DI", new String[]{"91", "01", "11"});
        classes.put("AD", new String[]{"91", "92", "11"});
        classes.put("AV", new String[]{"13"});
        classes.put("AW", new String[]{"11", "91"});
        classes.put("DW", new String[]{"01", "11"});
        classes.put("CE", new String[]{"01", "91"});
        classes.put("CI", new String[]{"01", "91"});

    }

    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        String educationProgram = data.get("educationProgram");

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

        for (int i = 0; i < classes.get(educationProgram).length; i++) {
            inlineKeyboard.addRow(new InlineKeyboardButton(educationProgram+classes.get(educationProgram)[i]).callbackData(
                    new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClassFinish").addData("class", educationProgram+classes.get(educationProgram)[i]).build()));
        }

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Bitte wähle deine Klasse:")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(inlineKeyboard);

        VertretungsPlanBot.getBot().execute(editMessageText);

        return true;
    }

    @Override
    public String getKey() {
        return "addClass";
    }
}
