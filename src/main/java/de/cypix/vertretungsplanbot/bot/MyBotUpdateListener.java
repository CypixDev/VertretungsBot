package de.cypix.vertretungsplanbot.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public class MyBotUpdateListener implements UpdatesListener {
    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            System.out.println("New Message ["+update.message().chat().id()+"] "+update.message().text());
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
