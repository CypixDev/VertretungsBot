package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class CallbackNotifyAddClassFinish implements KeyboardCallback {
    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        String className = data.get("class");

        SQLManager.insertNewNotification(chat.id(), className);

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(), "Benachrichtigungen für "+className+" wurden erfolgreich aktiviert!")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(new InlineKeyboardMarkup());

        VertretungsPlanBot.getBot().execute(editMessageText);
        for (VertretungsEntry allRelevantEntriesByClass : SQLManager.getAllRelevantEntriesByClass(className)) {
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(),
                    "Neuer eintrag für den " + allRelevantEntriesByClass.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n" +
                            "Klasse: " + allRelevantEntriesByClass.getClassName() + "\n" +
                            "Stunde: " + allRelevantEntriesByClass.getDefaultHour() + "\n" +
                            "Fach: " + allRelevantEntriesByClass.getDefaultSubject() + "\n" +
                            "Anmerkung: " + allRelevantEntriesByClass.getNote()));

        }

        return true;
    }

    @Override
    public String getKey() {
        return "addClassFinish";
    }
}
