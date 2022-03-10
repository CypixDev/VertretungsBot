package de.cypix.vertretungsplanbot.bot.remind;

import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterRemindManager {

    private Map<Long, String> map;


    public EnterRemindManager() {
        map = new HashMap<>();
    }

    public void startInput(long chatId, String className){
        map.put(chatId, className);
    }

    public void end(String input, long chatId){
        if(correctSyntax(input)){
            VertretungsPlanBot.getBot().execute(new SendMessage(chatId,
                    "Erfolgreich eine Erinnerung für "+map.get(chatId)+" um "+input.replace("T", "")+(input.startsWith("T") ? " am Vortag " : "")+" hinzugefügt!"));
            SQLManager.insertNewRemind(chatId, map.get(chatId), input);
        }else VertretungsPlanBot.getBot().execute(new SendMessage(chatId, "Nicht der richtige Syntax!"));
        map.remove(chatId);
    }

    public boolean isEnterMessage(long chatId){
        return map.containsKey(chatId);
    }


    private boolean correctSyntax(String str){
        try{
            if(str.length() == 6){
                if(str.startsWith("T")){
                    if(str.toCharArray()[3] == ':'){
                        str = str.replace("T", "");
                        if (checkInteger(str)) return true;
                    }
                }
            }else if(str.length() == 5){
                if (str.toCharArray()[2] == ':') {
                    if (checkInteger(str)) return true;
                }
            }
        }catch(Exception e){
            return false;
        }
        return false;
    }

    private boolean checkInteger(String str) {
        int hour = Integer.parseInt(str.split(":")[0]);
        int min = Integer.parseInt(str.split(":")[1]);

        if (hour < 24 && hour >= 0) {
            if (min < 60 && min >= 0) {
                return true;
            }
        }
        return false;
    }
}
