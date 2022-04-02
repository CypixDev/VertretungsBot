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
import de.cypix.vertretungsplanbot.sql.SQLManager;

public class CMDNotifyList implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        for (String className : SQLManager.getAllNotifyingClassesByChatId(chat.id())) {
            inlineKeyboard.addRow(new InlineKeyboardButton(className).callbackData(
                    new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "overview").addData("class", className).build()));
        }

        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Hier die Liste deiner Notifications:").replyMarkup(inlineKeyboard));
    }
}
