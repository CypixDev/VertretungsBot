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
            StringBuilder builder = new StringBuilder();
            builder.append("Neuer eintrag für den ")
                    .append(allRelevantEntriesByClass.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .append("\n");

            builder.append("Klasse: ").append(allRelevantEntriesByClass.getClassName()).append("\n");
            builder.append("Stunde: ").append(allRelevantEntriesByClass.getDefaultHour()).append("\n");
            builder.append("Fach: ").append(allRelevantEntriesByClass.getDefaultSubject()).append("\n");
            if(allRelevantEntriesByClass.getNote() != null && !allRelevantEntriesByClass.getNote().equals("null"))
                builder.append("Anmerkung: ").append(allRelevantEntriesByClass.getNote()).append("\n");
            if(allRelevantEntriesByClass.getNewTeacher() != null && !allRelevantEntriesByClass.getNewTeacher().equals("null"))
                builder.append("Vertreter: ").append(allRelevantEntriesByClass.getNewTeacher()).append("\n");
            if(allRelevantEntriesByClass.getNewSubject() != null && !allRelevantEntriesByClass.getNewSubject().equals("null"))
                builder.append("Neues Fach: ").append(allRelevantEntriesByClass.getNewSubject()).append("\n");
            if(allRelevantEntriesByClass.getNewRoom() != null && !allRelevantEntriesByClass.getNewRoom().equals("null"))
                builder.append("Neuer Raum: ").append(allRelevantEntriesByClass.getNewRoom()).append("\n");
            if(allRelevantEntriesByClass.getNewHour() != null && !allRelevantEntriesByClass.getNewHour().equals("null"))
                builder.append("Neue Stunde: ").append(allRelevantEntriesByClass.getNewHour()).append("\n");


            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), builder.toString()));

        }

        return true;
    }

    @Override
    public String getKey() {
        return "addClassFinish";
    }
}
