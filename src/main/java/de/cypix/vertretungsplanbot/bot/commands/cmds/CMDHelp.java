package de.cypix.vertretungsplanbot.bot.commands.cmds;


import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

public class CMDHelp implements TelegramCommand {

    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Hier ist deine Hilfe:\n" +
                "➡️ Benutzte /notify um Benachrichtigungen zu aktivieren\n" +
                "➡️ Wenn du /notifylist benutzt, kannst du alle Benachrichtigungen sehen und bearbeiten\n" +
                "➡️ Wenn du dich zusätzlich Erinnern lassen möchtest, verwende: /remind \n" +
                "➡️ Du möchstest alle einträge nochmal Gesendet bekommen ? -> /resend \n" +
                "➡️ Falls du alle gespeicherten personenbezogenen Daten haben willst, verwende /getalldata\n" +
                "➡️ Alle Daten kannst du mit /deletealldata löschen\n" +
                "➡️ Mit /status bekommst du den Status der verschiedenen Systeme\n\n" +
                "➡️ Mit /changelog sehe alle neuen Funktionen\n\n" +
                "Für eine kleine Spende: \npaypal.me/piusko"));

    }
}
