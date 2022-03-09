package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.remind;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class CallBackRemindAddRemind implements KeyboardCallback {

    /*
    Callback to add new remind
     */

    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        boolean dayBefore = data.get("hour").startsWith("T");
        try {
            LocalTime time = LocalTime.parse(data.get("hour").replace("T", ""), DateTimeFormatter.ISO_LOCAL_TIME);
            if(SQLManager.insertNewRemind(chat.id(), data.get("class"), data.get("hour"))){
                EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(),
                        "Erfolgreich eine Erinnerung für "+data.get("class")+" um "+time.format(DateTimeFormatter.ofPattern("HH:mm"))+(dayBefore ? " am Vortag " : "")+" hinzugefügt!")
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .replyMarkup(new InlineKeyboardMarkup());

                VertretungsPlanBot.getBot().execute(editMessageText);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getKey() {
        return "addRemind";
    }
}
