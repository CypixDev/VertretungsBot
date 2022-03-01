package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback;

import java.util.HashMap;
import java.util.Map;

public class KeyboardCallBackBuilder {

    private final HashMap<String, String> data;
    private final KeyboardCallbackType keyboardCallbackType;
    private final String key;

    public KeyboardCallBackBuilder(KeyboardCallbackType keyboardCallbackType, String key) {
        this.keyboardCallbackType = keyboardCallbackType;
        this.key = key;
        data = new HashMap<>();
    }

    public KeyboardCallBackBuilder addData(String key, String value){
        data.put(key, value);
        return this;
    }

    public String build(){
        StringBuilder str = new StringBuilder("type=keyboard").append(";");

        str.append("callbackType").append("=").append(keyboardCallbackType.ordinal()).append(";");
        str.append("key").append("=").append(key).append(";");

        for (Map.Entry<String, String> entry : data.entrySet()) {
            str.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return str.toString();
    }

    @Override
    public String toString() {
        return build();
    }
}
