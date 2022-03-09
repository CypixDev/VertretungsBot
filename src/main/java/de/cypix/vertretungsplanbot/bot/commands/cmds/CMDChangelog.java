package de.cypix.vertretungsplanbot.bot.commands.cmds;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

import java.io.IOException;
import java.util.Properties;

public class CMDChangelog implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {

        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Changelog für Version "+"1.2-BETA"+"\n" +
                "- Neue Befehle: /changelog, /deleteall, /remind, /resend\n" +
                "- Erinnerungen können erstellt werden mit /remind\n" +
                "- Code cleanup\n" +
                "- Bug fixes\n" +
                "- fixed some typos"));
    }
}
