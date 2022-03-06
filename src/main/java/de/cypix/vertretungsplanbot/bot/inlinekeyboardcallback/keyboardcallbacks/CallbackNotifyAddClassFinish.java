package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
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
            if(allRelevantEntriesByClass.getLastEntryUpdate().getNote() != null && !allRelevantEntriesByClass.getLastEntryUpdate().getNote().equals("null"))
                builder.append("Anmerkung: ").append(allRelevantEntriesByClass.getLastEntryUpdate().getNote()).append("\n");
            if(allRelevantEntriesByClass.getLastEntryUpdate().getTeacherLong() != null && !allRelevantEntriesByClass.getLastEntryUpdate().getTeacherLong().equals("null"))
                builder.append("Vertreter: ").append(allRelevantEntriesByClass.getLastEntryUpdate().getTeacherLong()).append("\n");
            if(allRelevantEntriesByClass.getLastEntryUpdate().getSubject() != null && !allRelevantEntriesByClass.getLastEntryUpdate().getSubject().equals("null"))
                builder.append("Neues Fach: ").append(allRelevantEntriesByClass.getLastEntryUpdate().getSubject()).append("\n");
            if(allRelevantEntriesByClass.getLastEntryUpdate().getRoom() != null && !allRelevantEntriesByClass.getLastEntryUpdate().getRoom().equals("null"))
                builder.append("Neuer Raum: ").append(allRelevantEntriesByClass.getLastEntryUpdate().getRoom()).append("\n");
            if(allRelevantEntriesByClass.getLastEntryUpdate().getHour() != null && !allRelevantEntriesByClass.getLastEntryUpdate().getHour().equals("null"))
                builder.append("Neue Stunde: ").append(allRelevantEntriesByClass.getLastEntryUpdate().getHour()).append("\n");


            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), builder.toString()));

        }

        return true;
    }

    @Override
    public String getKey() {
        return "addClassFinish";
    }
}
