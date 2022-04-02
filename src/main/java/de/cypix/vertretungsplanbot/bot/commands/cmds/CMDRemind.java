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
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.remind.CallBackRemindOpenAddRemind;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.remind.CallBackRemindOverviewReminds;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.util.List;

public class CMDRemind implements TelegramCommand {
    @Override
    public void performCommand(User user, Chat chat, Message message, String[] args) {

        List<String> listOfNotifies = SQLManager.getAllNotifyingClassesByChatId(chat.id());

        if(listOfNotifies.isEmpty()) {
            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Bitte abonniere erst eine Klasse"));
        }else if(listOfNotifies.size() == 1){
            String className = listOfNotifies.get(0);

            List<String> list = SQLManager.getAllReminderByClassAndChatId(className, chat.id());
            InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();


            if(list.isEmpty()) {
                //Open add page

                inlineKeyboard = CallBackRemindOpenAddRemind.getKeyBoard(className);

                VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Erinnerung hinzufügen:").replyMarkup(inlineKeyboard));


            }else{
                inlineKeyboard = CallBackRemindOverviewReminds.getKeyBoard(list, className);

                VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Hier die Liste deiner Erinnerungen für "+className+":").replyMarkup(inlineKeyboard));
            }
        }else {

            InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
            for (String className : listOfNotifies) {
                inlineKeyboard.addRow(new InlineKeyboardButton(className).callbackData(
                        new KeyboardCallBackBuilder(KeyboardCallbackType.REMIND, "overviewReminds").addData("class", className).build()));
            }

            VertretungsPlanBot.getBot().execute(new SendMessage(chat.id(), "Bitte wähle eine Klasse:").replyMarkup(inlineKeyboard));
        }
    }
}
