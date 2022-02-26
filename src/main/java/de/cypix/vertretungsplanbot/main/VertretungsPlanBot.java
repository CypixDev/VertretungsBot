package de.cypix.vertretungsplanbot.main;

import com.pengrad.telegrambot.TelegramBot;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackManager;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.CallbackNotifyDelete;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.CallbackNotifyOpenOverview;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.CallbackNotifyOverview;
import de.cypix.vertretungsplanbot.bot.listener.BotListener;
import de.cypix.vertretungsplanbot.bot.commands.CommandManager;
import de.cypix.vertretungsplanbot.bot.commands.cmds.*;
import de.cypix.vertretungsplanbot.configuration.ConfigManager;
import de.cypix.vertretungsplanbot.console.ConsoleManager;
import de.cypix.vertretungsplanbot.sql.SQLConnector;
import de.cypix.vertretungsplanbot.vertretungsplan.Updater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class VertretungsPlanBot {

    private static VertretungsPlanBot instance;

    private static TelegramBot bot;
    private static CommandManager commandManager;
    private static KeyboardCallbackManager keyBoardCallBackManager;
    private static SQLConnector sqlConnector;
    private static ConfigManager configManager;
    private static ConsoleManager consoleManager;
    private static Updater updater;
    public static Logger logger;

    public static void main(String[] args) {
        setupLogger();
        instance = new VertretungsPlanBot();
        configManager = new ConfigManager();
        consoleManager = new ConsoleManager();
        commandManager = new CommandManager();
        keyBoardCallBackManager = new KeyboardCallbackManager();
        updater = new Updater();
        consoleManager.start();

        //Register things
        registerCommands();
        registerKeyBoardCallBacks();

        if(configManager.isStatingAutomatically()){
            instance.startSQL();
            instance.startBot();
            instance.startUpdater();
        }
    }

    private void startUpdater() {
        getUpdater().start();
    }

    public void startBot(){
        String token = configManager.getToken();
        startBot(token);
    }
    public void startBot(String token){
        bot = new TelegramBot(token);
        bot.setUpdatesListener(new BotListener());
    }

    private static void registerCommands() {
        commandManager.registerCommand("/test", new CMDTest());
        commandManager.registerCommand("/help", new CMDHelp());
        commandManager.registerCommand("/start", new CMDStart());
        commandManager.registerCommand("/notify", new CMDNotify());
        commandManager.registerCommand("/notifylist", new CMDNotifyList());
    }

    private static void registerKeyBoardCallBacks(){
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyOverview());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyDelete());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyOpenOverview());
    }


    public void startSQL(){
        sqlConnector = new SQLConnector(true);
        System.out.println("Started SQL....");
    }

    private static void setupLogger() {
        logger = Logger.getLogger("Noyce");
        FileHandler fh;
        File file = new File("latest.log");
        new File("log").mkdirs();
        if(file.exists()){
            try {
                Path source = Paths.get("latest.log");
                Path target = Paths.get("log/"+calcDate(System.currentTimeMillis())+".log");
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else System.out.println("Log file not exists!");
        try {


            // This block configure the logger with handler and formatter
            fh = new FileHandler("latest.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String calcDate(long milliseconds) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyy_MMM_dd_HH_mm");
        Date resultDate = new Date(milliseconds);
        return date_format.format(resultDate);
    }


    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static SQLConnector getSqlConnector() {
        return sqlConnector;
    }

    public static TelegramBot getBot() {
        return bot;
    }

    public static VertretungsPlanBot getInstance() {
        return instance;
    }

    public static Updater getUpdater() {
        return updater;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static ConsoleManager getConsoleManager() {
        return consoleManager;
    }

    public static KeyboardCallbackManager getKeyboardCallbackManager() {
        return keyBoardCallBackManager;
    }
}
