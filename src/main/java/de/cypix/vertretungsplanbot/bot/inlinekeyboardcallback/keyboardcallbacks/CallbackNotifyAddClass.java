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
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallbackNotifyAddClass implements KeyboardCallback {

    private static final Logger logger = Logger.getLogger(CallbackNotifyAddClass.class);

    private final HashMap<String, String[]> classes;

    public CallbackNotifyAddClass() {
        classes = new HashMap<>();
        classes.put("AA", new String[]{"01", "11", "81", "91"});
        classes.put("AB", new String[]{"01", "02", "11", "12", "81", "91"});
        classes.put("AD", new String[]{"01", "02", "11", "12", "91", "92"});
        classes.put("AE", new String[]{"01", "11", "81", "91"});
        classes.put("AF", new String[]{"01", "11"});
        classes.put("AG", new String[]{"01", "11", "91"});
        classes.put("AI", new String[]{"01", "11", "81", "91"});
        classes.put("AK", new String[]{"01", "11", "81", "91"});
        classes.put("AM", new String[]{"01", "11", "81", "91"});
        classes.put("AP", new String[]{"01", "11", "81", "91"});
        classes.put("AV", new String[]{"11", "13"});
        classes.put("AW", new String[]{"01", "11", "81", "91"});
        classes.put("AZ", new String[]{"01", "02", "11", "12", "81", "82", "91", "92"});
        classes.put("B1", new String[]{"M11"});
        classes.put("B2", new String[]{"E11", "M11"});
        classes.put("CE", new String[]{"01", "11"});
        classes.put("CI", new String[]{"01", "11", "12", "91"});
        classes.put("CM", new String[]{"01", "11"});
        classes.put("DI", new String[]{"91", "01", "11"});
        classes.put("DW", new String[]{"01", "11"});
        classes.put("EE", new String[]{"01", "11", "81", "91"});
        classes.put("EM", new String[]{"01", "11", "81", "91"});

    }

    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if (!getKey().equalsIgnoreCase(key)) return false;
        String educationProgram = data.get("educationProgram");

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

        switch (classes.get(educationProgram).length) {

            case 1:
            case 2:
            case 3:
                InlineKeyboardButton[] buttons = new InlineKeyboardButton[classes.get(educationProgram).length];
                for (int i = 0; i < classes.get(educationProgram).length; i++) {
                    buttons[i] = new InlineKeyboardButton(educationProgram + classes.get(educationProgram)[i]).callbackData(
                            new KeyboardCallBackBuilder(
                                    KeyboardCallbackType.NOTIFY, "addClassFinish")
                                    .addData("class", educationProgram + classes.get(educationProgram)[i])
                                    .build());
                }
                inlineKeyboard.addRow(buttons);
                break;

            case 4:
            case 6:
            case 8:
                for (int i = 0; i < classes.get(educationProgram).length; i += 2) {
                    inlineKeyboard.addRow(
                            new InlineKeyboardButton(educationProgram + classes.get(educationProgram)[0]).callbackData(
                                    new KeyboardCallBackBuilder(
                                            KeyboardCallbackType.NOTIFY, "addClassFinish")
                                            .addData("class", educationProgram + classes.get(educationProgram)[0])
                                            .build()),
                            new InlineKeyboardButton(educationProgram + classes.get(educationProgram)[1]).callbackData(
                                    new KeyboardCallBackBuilder(
                                            KeyboardCallbackType.NOTIFY, "addClassFinish")
                                            .addData("class", educationProgram + classes.get(educationProgram)[1])
                                            .build()));
                }

                break;
            default:
                logger.error("Show all classes faild because length is: " + classes.get(educationProgram).length);
                break;


        }

        inlineKeyboard.addRow(new InlineKeyboardButton("Zurück")
                .callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "openAddOverview").build()));

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
