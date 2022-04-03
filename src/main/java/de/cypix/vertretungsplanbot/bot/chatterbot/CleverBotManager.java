package de.cypix.vertretungsplanbot.bot.chatterbot;

import com.google.code.chatterbotapi.ChatterBotFactory;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallBackBuilder;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CleverBotManager {

    private static final Logger logger = Logger.getLogger(CleverBotManager.class);

    private static final int EXPIRE_HOUR = 2;

    public final ChatterBotFactory chatterBotFactory;
    private final List<MyBotSession> sessions;

    public CleverBotManager() {
        this.chatterBotFactory = new ChatterBotFactory();
        sessions = new ArrayList<>();
    }

    public void checkOutdated(){
        LocalDateTime nowMinus2H = LocalDateTime.now().minusHours(EXPIRE_HOUR);
        for (MyBotSession session : sessions) {
            if(session.getLastSendMessage().isBefore(nowMinus2H)){
                logger.info("Clever-bot session expired ["+session.getChatId()+"]");
            }
        }
        sessions.removeIf(session -> session.getLastSendMessage().isBefore(nowMinus2H));

    }

    public boolean hasSession(long chatId) {
        for (MyBotSession session : sessions) {
            if(session.getChatId() == chatId) return true;
        }
        return false;
    }

    public void createSession(long chatId) {
        sessions.add(new MyBotSession(chatId, chatterBotFactory));
        logger.info("New Clever-bot session created ["+chatId+"]");
    }

    public MyBotSession getSession(long chatId){
        for (MyBotSession session : sessions) {
            if(session.getChatId() == chatId) return session;
        }
        return null;
    }

    public void handleMessage(Long chatId, String message){
        if(hasSession(chatId)){
            VertretungsPlanBot.getBot().execute(new SendMessage(chatId, getSession(chatId).getAnswer(message)));
        }else{
            if(SQLManager.hasAcceptedCleverBot(chatId)){
                try {
                    createSession(chatId);
                    VertretungsPlanBot.getBot().execute(new SendMessage(chatId, getSession(chatId).getAnswer(message)));
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
}
