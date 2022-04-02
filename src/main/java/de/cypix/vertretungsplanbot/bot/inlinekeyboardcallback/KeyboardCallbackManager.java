package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import de.cypix.vertretungsplanbot.vertretungsplan.Updater;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyboardCallbackManager {

    public ConcurrentHashMap<KeyboardCallback, KeyboardCallbackType> callbacks;

    private static final Logger logger = Logger.getLogger(KeyboardCallbackManager.class);

    public KeyboardCallbackManager() {
        this.callbacks = new ConcurrentHashMap<>();
    }
    public void registerCallBack(KeyboardCallbackType keyboardCallbackType, KeyboardCallback keyboardCallback){
        this.callbacks.put(keyboardCallback, keyboardCallbackType);
    }
    //returns false if command not exists
    public boolean handle(KeyboardCallbackType callbackType, String key, Update update, Chat chat, HashMap<String, String> data){
        logger.log(Level.INFO, "New Callback[callbackType"+callbackType.name()+";Key="+key+";Data"+data.toString());
        for (Map.Entry<KeyboardCallback, KeyboardCallbackType> entry : callbacks.entrySet()) {
            if(entry.getValue().equals(callbackType)){
                if(entry.getKey().handleCallBack(key, update, chat, data))
                    return true;
            }
        }
        return false;
    }
}
