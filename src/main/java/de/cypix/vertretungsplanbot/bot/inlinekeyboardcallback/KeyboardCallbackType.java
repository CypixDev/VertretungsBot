package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback;

public enum KeyboardCallbackType {

    NOTIFY, SETTING, REMIND, DELETE_ALL, CLEVERBOT;

    public static KeyboardCallbackType valueOf(int id){
        for (KeyboardCallbackType value : values()) {
            if(value.ordinal() == id) return value;
        }
        return null;
    }

}
