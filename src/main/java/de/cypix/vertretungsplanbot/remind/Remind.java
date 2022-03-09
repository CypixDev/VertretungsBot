package de.cypix.vertretungsplanbot.remind;

import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntryUpdate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Remind {
    private long chat_id;
    private String className;
    private String hour;

    public Remind(long chat_id, String className, String hour) {
        this.chat_id = chat_id;
        this.className = className;
        this.hour = hour;
    }

    public long getChat_id() {
        return chat_id;
    }

    public String getClassName() {
        return className;
    }

    public String getHour() {
        return hour;
    }

    public LocalTime getLocalTime(){
        return LocalTime.parse(hour.replace("T", ""), DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public boolean isNextDay(){
        return hour.startsWith("T");
    }
}
