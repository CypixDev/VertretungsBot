package de.cypix.vertretungsplanbot.bot.commands.cmds;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;

public class CMDNotify implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        if(args.length == 2){
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "YESS! Du bist also in der "+args[1]));
            //TODO: class exists?
            SQLManager.insertNewNotification(chat.id(), args[1]);

            //send all relevant entries
            for (VertretungsEntry allRelevantEntriesByClass : SQLManager.getAllRelevantEntriesByClass(args[1])) {
                VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Änderung für: "+allRelevantEntriesByClass.getDefaultSubject()));
            }
        }else {
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Das hast du sau mäßig verkackt!"));

        }
    }
}
