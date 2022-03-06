package de.cypix.vertretungsplanbot.console;

import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.vertretungsplan.Updater;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleManager extends Thread{

    private Scanner scanner;

    private static final Logger logger = Logger.getLogger( ConsoleManager.class );

    @Override
    public void run() {
        try {
            startConsole();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConsoleManager(){ }

    private void startConsole() throws IOException {
        scanner = new Scanner(System.in);

        logger.info("Console scanner started successfully");

        while(scanner.hasNext()){
            handleConsoleInput(scanner.nextLine());
        }
    }

    private void handleConsoleInput(String input) throws IOException {
        String[] args = input.split(" ");
        if(args.length == 0){
            return;
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("maintenance")){
                if(VertretungsPlanBot.getConfigManager().isMaintenance()){
                    System.out.println("Activ!");
                }else System.out.println("Nicht activ");
                return;
            }
            if(args[0].equalsIgnoreCase("update")){
                Updater.filter();
                System.out.println("updated!");
            }
            if(args[0].equalsIgnoreCase("status")){
                System.out.println("SQL is "+(VertretungsPlanBot.getSqlConnector() != null && VertretungsPlanBot.getSqlConnector().isConnected() ? "connected" : "disconnected"));
                //System.out.println("Bot is "+(VertretungsPlanBot.getBot(). ? "Offline(400ms)" : "Online"));
                return;
            }
            if(args[0].equalsIgnoreCase("stop")){
                System.out.println("Shutting everything down....");
                VertretungsPlanBot.getSqlConnector().closeConnection();
                //TasksCheckBot.getJda().shutdownNow();
                System.exit(1);
                return;
            }
        }
        if(args.length == 2){
            if(args[0].equalsIgnoreCase("simulate")){
                if(args[1].equalsIgnoreCase("loss")){
                    Updater.simulateLoss = true;
                    System.out.println("Simulating loss....");
                    return;
                }
                if(args[1].equalsIgnoreCase("update")){
                    Updater.simulateUpdate = true;
                    System.out.println("Simulating update....");
                    return;
                }
                if(args[1].equalsIgnoreCase("add")){
                    Updater.simulateAdd = true;
                    System.out.println("Simulating add....");
                    return;
                }
            }
            if(args[0].equalsIgnoreCase("start")){
                if(args[1].equalsIgnoreCase("bot")){
                    VertretungsPlanBot.getInstance().startBot();
                    return;
                }
                if(args[1].equalsIgnoreCase("sql")){
                    VertretungsPlanBot.getInstance().startSQL();
                    return;
                }
                if(args[1].equalsIgnoreCase("all")){
                    VertretungsPlanBot.getInstance().startSQL();
                    VertretungsPlanBot.getInstance().startBot();
                    return;
                }
                if(args[1].equalsIgnoreCase("updater")){
                    VertretungsPlanBot.getUpdater().start();
                }
            }
            if(args[0].equalsIgnoreCase("stop")){
                if(args[1].equalsIgnoreCase("sql")){
                    VertretungsPlanBot.getSqlConnector().closeConnection();
                    System.out.println("Closed connection to sql");
                    return;
                }
                if(args[1].equalsIgnoreCase("bot")){
                    //TasksCheckBot.getJda().shutdownNow();
                    System.out.println("Bot is now offline!");
                    return;
                }
            }
        }
        if(args.length == 3){
            if(args[0].equalsIgnoreCase("start")){
                if(args[1].equalsIgnoreCase("bot")){
                    String token = args[2];
                    VertretungsPlanBot.getInstance().startBot(token);
                    System.out.println("Start Bot....");
                    return;
                }
            }
        }
        if(args.length >= 3){
      /*      if(args[0].equalsIgnoreCase("write")){
                String tag = args[1];
                StringBuilder message = new StringBuilder();
                for (int i = 2; i < args.length; i++)
                    message.append(args[i]).append(" ");*//*
                TasksCheckBot.getJda().openPrivateChannelById(tag).queue(e -> {
                    e.sendMessage(message.toString()).queue();
                });*//*
                for (PrivateChannel privateChannel : TasksCheckBot.getJda().getPrivateChannels()) {
                    if(privateChannel.getUser().getAsTag().equals(tag)) {
                        privateChannel.sendMessage(message.toString()).queue();
                    }
                }
                return;
            }*/
        }
        sendHelp();
    }

    private void sendHelp() {
        System.out.println("No Help for you!");
    }

}
