package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyboardCallbackManager {

    public ConcurrentHashMap<KeyboardCallback, KeyboardCallbackType> callbacks;

    public KeyboardCallbackManager() {
        this.callbacks = new ConcurrentHashMap<>();
    }
    public void registerCallBack(KeyboardCallbackType keyboardCallbackType, KeyboardCallback keyboardCallback){
        this.callbacks.put(keyboardCallback, keyboardCallbackType);
    }
    //returns false if command not exists
    public boolean handle(KeyboardCallbackType callbackType, String key, User user, Chat chat, HashMap<String, String> data){
        for (Map.Entry<KeyboardCallback, KeyboardCallbackType> entry : callbacks.entrySet()) {
            if(entry.getValue().equals(callbackType)){
                if(entry.getKey().handleCallBack(key, user, chat, data))
                    return true;
            }
        }
        return false;
    }
}
