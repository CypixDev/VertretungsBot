package de.cypix.vertretungsplanbot.bot.commands;


import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

public interface TelegramCommand {

    public void performCommand(User user, Chat chat, Message message, String[] args);

}
