package de.cypix.vertretungsplanbot.bot.chatterbot;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MyBotSession {

    private static final Logger logger = Logger.getLogger(MyBotSession.class);


    private long chatId;
    private LocalDateTime lastSendMessage;
    private ChatterBotFactory chatterBotFactory;
    private ChatterBot chatterBot;
    private ChatterBotSession chatterBotSession;


    public MyBotSession(long chatId, ChatterBotFactory chatterBotFactory) {
        this.chatId = chatId;
        this.lastSendMessage = LocalDateTime.now();
        this.chatterBotFactory = chatterBotFactory;

        create();
    }

    public void create(){
        try {
            chatterBot = chatterBotFactory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
            chatterBotSession = chatterBot.createSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getAnswer(String input){
        lastSendMessage = LocalDateTime.now();
        try {
            String output = chatterBotSession.think(input);
            log(input, output);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void log(String input, String output){
        SQLManager.insertNewConversation(chatId, input, output);
        logger.info("Clever-Bot conversation ["+chatId+"] "+input+" -> "+output);
    }

    public long getChatId() {
        return chatId;
    }

    public LocalDateTime getLastSendMessage() {
        return lastSendMessage;
    }
}
