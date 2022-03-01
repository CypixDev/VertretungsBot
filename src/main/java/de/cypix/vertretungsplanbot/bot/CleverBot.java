package de.cypix.vertretungsplanbot.bot;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

public class CleverBot {

    private static ChatterBotFactory factory = new ChatterBotFactory();

    private static ChatterBot bot1;

    static {
        try {
            bot1 = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ChatterBotSession cleverBotSession = bot1.createSession();



    public static String getAnswer(String message) throws Exception {
        return cleverBotSession.think(message);
    }

}
