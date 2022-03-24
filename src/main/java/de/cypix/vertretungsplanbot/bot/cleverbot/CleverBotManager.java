package de.cypix.vertretungsplanbot.bot.cleverbot;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.CleverBot;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallBackBuilder;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

public class CleverBotManager {


    public void handleMessage(Long chatId, String message){
        if(SQLManager.hasAcceptedCleverBot(chatId)){
            try {
                VertretungsPlanBot.getBot().execute(new SendMessage(chatId, CleverBot.getAnswer(message)));
            } catch (Exception e) {
                VertretungsPlanBot.getBot().execute(new SendMessage(chatId, "Nicht bekannt!"));
            }
        }else{
            InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();

            inlineKeyboard.addRow(new InlineKeyboardButton("Ja")
                        .callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.CLEVERBOT, "agree").addData("decision", "yes").build()),
                    new InlineKeyboardButton("Nein")
                            .callbackData(new KeyboardCallBackBuilder(KeyboardCallbackType.CLEVERBOT, "agree").addData("decision", "no").build()));

            VertretungsPlanBot.getBot().execute(new SendMessage(chatId,
                    "Du hast keinen '/' verwendet...\n" +
                            "Wenn du eine Unterhaltung mit dem 'CleverBot' führen möchtest, drücke Ja.\n" +
                    "Bist du sicher, dass du mit dem 'CleverBot' schreiben möchtest?").replyMarkup(inlineKeyboard));

        }

    }


}
