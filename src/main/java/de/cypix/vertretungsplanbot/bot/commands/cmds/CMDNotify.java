package de.cypix.vertretungsplanbot.bot.commands.cmds;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallBackBuilder;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;

import java.time.format.DateTimeFormatter;

public class CMDNotify implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        //VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "YESS! Du bist also in der " + args[1]));
        //TODO: class exists?
        //SQLManager.insertNewNotification(chat.id(), args[1]);
                /*
            Klassen:
            DI
            AD
            AV
            AW
            CE
            CI
            DW
             */
        String[] classes = {"DI", "AD", "AV", "CE", "CI", "DW", "AW"};
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        inlineKeyboard.addRow(new InlineKeyboardButton(classes[0]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[0]).build()),
                new InlineKeyboardButton(classes[1]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[1]).build()),
                new InlineKeyboardButton(classes[2]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[2]).build()));
        inlineKeyboard.addRow(new InlineKeyboardButton(classes[3]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[3]).build()),
                new InlineKeyboardButton(classes[4]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[4]).build()),
                new InlineKeyboardButton(classes[5]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[5]).build()));
        inlineKeyboard.addRow(new InlineKeyboardButton(classes[6]).callbackData(
                new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[6]).build()));

        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Bitte wähle einen Bildungsgang").replyMarkup(inlineKeyboard));

/*        for (String className : SQLManager.getAllNotifiesByChatId(chat.id())) {
            new InlineKeyboardButton(className).callbackData(
                    new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "add").addData("class", className).build());
        }*/


/*        //send all relevant entries
        for (VertretungsEntry allRelevantEntriesByClass : SQLManager.getAllRelevantEntriesByClass(args[1])) {
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(),
                    "Neuer eintrag Für den " + allRelevantEntriesByClass.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n" +
                            "Klasse: " + allRelevantEntriesByClass.getClassName() + "\n" +
                            "Stunde: " + allRelevantEntriesByClass.getDefaultHour() + "\n" +
                            "Fach: " + allRelevantEntriesByClass.getDefaultSubject() + "\n" +
                            "Anmerkung: " + allRelevantEntriesByClass.getNote()));
        }*/
    }
}
