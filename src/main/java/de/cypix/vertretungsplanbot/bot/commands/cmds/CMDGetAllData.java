package de.cypix.vertretungsplanbot.bot.commands.cmds;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

public class CMDGetAllData implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        StringBuilder str = new StringBuilder("Hier alle deine Daten: ").append("\n");
        for (String allDatum : SQLManager.getAllData(chat.id())) {
            str.append(allDatum).append("\n");
        }
        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), str.toString()));
    }
}
