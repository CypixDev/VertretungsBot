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

public class CMDNotify implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {
        String[] classes = {"AA", "AB", "AD", "AE", "AF", "AG", "AI", "AK", "AM", "AP", "AV", "AW", "AZ",
                "B1", "B2", "CE", "CI", "CM", "DI", "DW", "EE", "EM"};
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

        for (int i = 0; i + 4 < classes.length; i += 4) {
            inlineKeyboard.addRow(new InlineKeyboardButton(classes[i]).callbackData(
                            new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[i]).build()),
                    new InlineKeyboardButton(classes[i + 1]).callbackData(
                            new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[i + 1]).build()),
                    new InlineKeyboardButton(classes[i + 2]).callbackData(
                            new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[i + 2]).build()),
                    new InlineKeyboardButton(classes[i + 3]).callbackData(
                            new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[i + 3]).build()));

        }
        inlineKeyboard.addRow(
                new InlineKeyboardButton(classes[classes.length - 2]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[classes.length - 2]).build()),
                new InlineKeyboardButton(classes[classes.length - 1]).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.NOTIFY, "addClass").addData("educationProgram", classes[classes.length - 1]).build()));


        VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Bitte wähle einen Bildungsgang").replyMarkup(inlineKeyboard));

    }
}
