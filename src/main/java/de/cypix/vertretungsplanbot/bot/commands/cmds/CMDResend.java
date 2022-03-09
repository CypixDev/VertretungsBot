package de.cypix.vertretungsplanbot.bot.commands.cmds;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;

public class CMDResend implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        for (String s : SQLManager.getAllNotifiesByChatId(chat.id())) {
            for (VertretungsEntry allRelevantEntriesByClass : SQLManager.getAllRelevantEntriesByClass(s)) {
                VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), allRelevantEntriesByClass.getSendUpdateMessage("Eintrag")));
            }
        }

    }
}
