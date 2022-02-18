package de.cypix.vertretungsplanbot.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

import java.util.List;

public class MyBotUpdateListener implements UpdatesListener {
    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            String message = update.message().text();
            String[] args = message.split(" ");
            //call command
            if (!VertretungsPlanBot.getCommandManager().perform(args[0], update.message().from(), update.message().chat(), update.message(), args)) {
                VertretungsPlanBot.getBot().execute(new SendMessage(update.message().chat().id(), "Befehl nicht bekannt!"));
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
