package de.cypix.vertretungsplanbot.bot.listener;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.bot.CleverBot;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.util.HashMap;
import java.util.List;

public class BotListener implements UpdatesListener {
    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            //Check if it's a message or keyboard callback
            if(update.message() != null){
                //It's probably a message
                String message = "";
                String[] args = {""};

                if (update.message().text() != null) {
                    System.out.println("Incoming message: " + update.message().text());
                    message = update.message().text();
                    args = message.split(" ");
                }

                if(message.startsWith("/")){
                    //call command
                    if (SQLManager.isRegistered(update.message().chat().id()) || args[0].equalsIgnoreCase("/start")) {
                        if (!VertretungsPlanBot.getCommandManager().perform(args[0], update.message().from(), update.message().chat(), update.message(), args)) {
                            VertretungsPlanBot.getBot().execute(new SendMessage(update.message().chat().id(), "Befehl nicht bekannt!"));
                        }
                        //Eigentlich unnötig glaube ich....
                    } else
                        VertretungsPlanBot.getBot().execute(new SendMessage(update.message().chat().id(), "Bitte registriere dich zuerst mit /start"));
                }else {
                    try {
                        VertretungsPlanBot.getBot().execute(new SendMessage(update.message().chat().id(), CleverBot.getAnswer(message)));
                    } catch (Exception e) {
                        VertretungsPlanBot.getBot().execute(new SendMessage(update.message().chat().id(), "Nicht bekannt!"));
                    }
                }

            }else if(update.callbackQuery() != null){
                //It's probably a callback
                if(update.callbackQuery().data().startsWith("type=keyboard;")){
                    String[] splitData = update.callbackQuery().data().split(";");
                    HashMap<String, String> data = new HashMap<>();

                    /* Splitting in segments for better reading
                    Starting at 1 because first identifier is keyboard
                    Example Key=banana;chatId=33333;*/
                    for (int i = 1; i < splitData.length; i++) {
                        data.put(splitData[i].split("=")[0], splitData[i].split("=")[1]);
                    }
                    if (SQLManager.isRegistered(update.callbackQuery().from().id())) {
                        if (!VertretungsPlanBot.getKeyboardCallbackManager().handle(
                                KeyboardCallbackType.valueOf(Integer.parseInt(data.get("callbackType"))),
                                data.get("key"),
                                update,
                                update.callbackQuery().message().chat(),
                                data)) {
                            VertretungsPlanBot.getBot().execute(new SendMessage(update.callbackQuery().from().id(), "Callback nicht bekannt!"));
                        }
                        //Eigentlich unnötig glaube ich....
                    } else
                        VertretungsPlanBot.getBot().execute(new SendMessage(update.callbackQuery().from().id(), "Bitte regestriere dich zuerst mit /start"));
                }else System.out.println("Called unknown callback, Data:"+update.callbackQuery().data());
            }else{
                System.out.println("Unknown update...");
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
