package de.cypix.vertretungsplanbot.bot.commands.cmds;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.commands.TelegramCommand;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallBackBuilder;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

public class CMDDeleteAllData implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {

        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

        inlineKeyboard.addRow(
                new InlineKeyboardButton("Ja").callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.DELETE_ALL, "confirm").addData("decision", "yes").build()),
                new InlineKeyboardButton("Nein").callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.DELETE_ALL, "confirm").addData("decision", "no").build()));


        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Bist du dir Sicher?").replyMarkup(inlineKeyboard));

    }
}
