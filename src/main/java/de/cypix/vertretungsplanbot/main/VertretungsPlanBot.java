package de.cypix.vertretungsplanbot.main;

import com.pengrad.telegrambot.TelegramBot;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackManager;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.*;
import de.cypix.vertretungsplanbot.bot.listener.BotListener;
import de.cypix.vertretungsplanbot.bot.commands.CommandManager;
import de.cypix.vertretungsplanbot.bot.commands.cmds.*;
import de.cypix.vertretungsplanbot.configuration.ConfigManager;
import de.cypix.vertretungsplanbot.console.ConsoleManager;
import de.cypix.vertretungsplanbot.sql.SQLConnector;
import de.cypix.vertretungsplanbot.vertretungsplan.Updater;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;


import java.text.SimpleDateFormat;
import java.util.Date;

public class VertretungsPlanBot {

    private static VertretungsPlanBot instance;

    private static TelegramBot bot;
    private static CommandManager commandManager;
    private static KeyboardCallbackManager keyBoardCallBackManager;
    private static SQLConnector sqlConnector;
    private static ConfigManager configManager;
    private static ConsoleManager consoleManager;
    private static Updater updater;
    private static Logger logger = Logger.getRootLogger();


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
        commandManager.registerCommand("/status", new CMDStatus());
        commandManager.registerCommand("/getalldata", new CMDGetAllData());
    }

    private static void registerKeyBoardCallBacks(){
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyOverview());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyDelete());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyOpenOverview());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyAddClass());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyAddClassFinish());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyOpenAddOverview());
    }


    public void startSQL(){
        sqlConnector = new SQLConnector(true);
        System.out.println("Started SQL....");
    }

    private static void setupLogger() {
        try {
            DOMConfigurator.configureAndWatch( "log4j.xml", 60*1000 );
/*            PatternLayout layout = new PatternLayout( "%d{ISO8601} %-5p [%t] %c: %m%n" );
            DailyRollingFileAppender fileAppender =
                    new DailyRollingFileAppender( layout, "logs/MeineLogDatei.log", "'.'yyyy-MM-dd_HH-mm" );
            logger.addAppender( fileAppender );
            logger.setLevel( Level.ALL );
            logger.*/
        } catch( Exception ex ) {
            System.out.println( ex );
        }
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
