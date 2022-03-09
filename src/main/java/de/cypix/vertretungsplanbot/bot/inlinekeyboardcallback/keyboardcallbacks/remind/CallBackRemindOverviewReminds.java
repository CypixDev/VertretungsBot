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
import java.util.List;

public class CallBackRemindOverviewReminds implements KeyboardCallback {

    /*
    Hier wird die übersicht aller Reminds für
     */
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        String className = data.get("class");

        List<String> list = SQLManager.getAllReminderByClassAndChatId(className, chat.id());
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();


        if(list.isEmpty()) {
            //Open add page

            EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Erinnerung  hinzufügen:")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(CallBackRemindOpenAddRemind.getKeyBoard(className));

            VertretungsPlanBot.getBot().execute(editMessageText);

        }else{
            for (String hour : list) {
                inlineKeyboard.addRow(new InlineKeyboardButton(hour).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "deleteRemind")
                                .addData("class", className)
                                .addData("hour", hour).build()));
            }
            inlineKeyboard.addRow(new InlineKeyboardButton("Zurück").callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "openOverview")
                            .build()),
                    new InlineKeyboardButton("Hinzufügen").callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "openAddRemind")
                            .addData("class", className)
                            .build()));

            EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Hier die Liste deiner Erinnerungen:")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(inlineKeyboard);

            VertretungsPlanBot.getBot().execute(editMessageText);
        }

        return true;
    }

    @Override
    public String getKey() {
        return "overviewReminds";
    }
}
