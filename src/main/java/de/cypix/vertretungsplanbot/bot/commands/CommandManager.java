package de.cypix.vertretungsplanbot.bot.commands;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    public ConcurrentHashMap<String, TelegramCommand> commands;

    public CommandManager() {
        this.commands = new ConcurrentHashMap<>();
    }
    public void registerCommand(String cmdName, TelegramCommand telegramCommandClass){
        this.commands.put(cmdName, telegramCommandClass);
    }
    //returns false if command not exists
    public boolean perform(String command, User user, Chat chat, Message message, String[] args){
        if(commands.containsKey(command.toLowerCase())){
            this.commands.get(command.toLowerCase()).performCommand(user, chat, message, args);
            return true;
        }
        return false;
    }
}
