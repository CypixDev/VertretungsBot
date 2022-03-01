package de.cypix.vertretungsplanbot.bot.commands.cmds;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

public class CMDStatus implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Status der Systeme:\n" +
                "\n" +
                "Telegrambot ✅\n" +
                "Updater "+(VertretungsPlanBot.getUpdater().isAlive() ? "✅" : "❌")+"\n" +
                "Datenbank "+(VertretungsPlanBot.getSqlConnector().isConnected() ? "✅" : "❌")+"\n" +
                "Discord "+"❌ (to do)\n" +
                "Signal "+"❌ (to do)\n"));
    }
}
