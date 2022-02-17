package de.cypix.vertretungsplanbot.configuration;

public class ConfigManager {

    public ConfigManager(){

        Config.load("config.json");

/*
        System.out.println("Token: "+Config.getInstance().token);
        System.out.println("ChannelName: "+Config.getInstance().channelName);
        System.out.println("Password: "+Config.getInstance().sqlPassword);
        System.out.println("Guild Name: "+Config.getInstance().guildName);
*/

        // Speichern der Konfigurationsdatei
        Config.getInstance().toFile("config.json");
        System.out.println("Successfully initialized config....");

    }

    public String getToken(){
        return Config.getInstance().token;
    }

    public String getSQLHost() {return Config.getInstance().sqlHost;}
    public String getSQLPassword() {return Config.getInstance().sqlPassword;}
    public String getSQLUser() {return Config.getInstance().sqlUser;}
    public String getSQLDatabase() {return Config.getInstance().sqlDatabase;}
    public int getSQLPort() {return Config.getInstance().sqlPort;}

    public boolean isStatingAutomatically(){return Config.getInstance().startAutomatically;}


    public String getChannelName(){return Config.getInstance().channelName;}

    public String getGuildName(){return Config.getInstance().guildName;}
}
