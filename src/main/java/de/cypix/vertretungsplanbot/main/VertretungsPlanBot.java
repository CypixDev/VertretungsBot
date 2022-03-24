package de.cypix.vertretungsplanbot.main;

import com.pengrad.telegrambot.TelegramBot;
import de.cypix.vertretungsplanbot.bot.cleverbot.CleverBotManager;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackManager;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallbackType;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.cleverbot.CallbackCleverBotAgree;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.delete_all.CallbackDeleteAllConfirmation;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.notify.*;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.remind.*;
import de.cypix.vertretungsplanbot.bot.listener.BotListener;
import de.cypix.vertretungsplanbot.bot.commands.CommandManager;
import de.cypix.vertretungsplanbot.bot.commands.cmds.*;
import de.cypix.vertretungsplanbot.bot.remind.EnterRemindManager;
import de.cypix.vertretungsplanbot.configuration.ConfigManager;
import de.cypix.vertretungsplanbot.console.ConsoleManager;
import de.cypix.vertretungsplanbot.remind.RemindScheduler;
import de.cypix.vertretungsplanbot.sql.SQLConnector;
import de.cypix.vertretungsplanbot.vertretungsplan.Updater;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class VertretungsPlanBot {

    private static VertretungsPlanBot instance;

    private static TelegramBot bot;
    private static CommandManager commandManager;
    private static KeyboardCallbackManager keyBoardCallBackManager;
    private static SQLConnector sqlConnector;
    private static ConfigManager configManager;
    private static ConsoleManager consoleManager;
    private static Updater updater;
    private static RemindScheduler remindScheduler;
    private static EnterRemindManager enterRemindManager;
    private static CleverBotManager cleverBotManager;
    private static final Logger logger = Logger.getRootLogger();

    /*
    TODO:
    - Verify system if using multiple platforms like discord
        - Check with Email- address, sending email with code and verify using Telegram/Discord
     */

    public static void main(String[] args) {
        setupLogger();
        instance = new VertretungsPlanBot();
        configManager = new ConfigManager();
        consoleManager = new ConsoleManager();
        commandManager = new CommandManager();
        keyBoardCallBackManager = new KeyboardCallbackManager();
        updater = new Updater();
        enterRemindManager = new EnterRemindManager();
        cleverBotManager = new CleverBotManager();
        remindScheduler = new RemindScheduler();
        consoleManager.start();

        //Register things
        registerCommands();
        registerKeyBoardCallBacks();

        if(configManager.isStatingAutomatically()){
            instance.startSQL();
            instance.startBot();
            instance.startSchedulers();
        }
        if(configManager.isMaintenance()){
            logger.warn("Maintenance is activated");
        }
    }

    private void startSchedulers() {
        getUpdater().start();
        getRemindScheduler().start();
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
        commandManager.registerCommand("/deletealldata", new CMDDeleteAllData());
        commandManager.registerCommand("/remind", new CMDRemind());
        commandManager.registerCommand("/resend", new CMDResend());
        commandManager.registerCommand("/changelog", new CMDChangelog());
    }

    private static void registerKeyBoardCallBacks(){
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyOverview());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyDelete());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyOpenOverview());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyAddClass());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyAddClassFinish());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.NOTIFY, new CallbackNotifyOpenAddOverview());


        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindAddRemind());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindDeleteRemind());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindEnterRemind());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindOpenAddRemind());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindOverviewReminds());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindOverview());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindOverviewReminds());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindOpenOverview());
        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.REMIND, new CallBackRemindOpenOverviewReminds());

        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.DELETE_ALL, new CallbackDeleteAllConfirmation());

        keyBoardCallBackManager.registerCallBack(KeyboardCallbackType.CLEVERBOT, new CallbackCleverBotAgree());
    }


    public void startSQL(){
        sqlConnector = new SQLConnector(true);
       logger.info("Started SQL....");
    }

    private static void setupLogger() {
        try {
            DOMConfigurator.configureAndWatch( "log4j.xml", 60*1000 );
        } catch( Exception ex ) {
            logger.error(ex);
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

    public static RemindScheduler getRemindScheduler() {
        return remindScheduler;
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

    public static EnterRemindManager getEnterMessageManager() {
        return enterRemindManager;
    }

    public static CleverBotManager getCleverBotManager() {
        return cleverBotManager;
    }
}
