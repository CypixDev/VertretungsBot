package de.cypix.vertretungsplanbot.vertretungsplan;

import de.cypix.vertretungsplanbot.sql.SQLManager;
import de.cypix.vertretungsplanbot.sql.UpdateType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VertretungsEntry implements Comparable<VertretungsEntry> {

    private int entryId;
    private final LocalDateTime registrationTimeStamp;
    private final LocalDate representationDate;
    private final String className;
    private final String defaultHour;
    private final String defaultRoom;
    private final String defaultTeacher;
    private final String defaultSubject;
    private VertretungsEntryUpdate lastEntryUpdate;


    public VertretungsEntry(int entryId, LocalDateTime registrationTimeStamp, LocalDate representationDate, String className, String defaultHour, String defaultRoom, String defaultTeacher, String defaultSubject, VertretungsEntryUpdate lastEntryUpdate) {
        this.entryId = entryId;
        this.registrationTimeStamp = registrationTimeStamp;
        this.representationDate = representationDate;
        this.className = className;
        this.defaultHour = defaultHour;
        this.defaultRoom = defaultRoom;
        this.defaultTeacher = defaultTeacher;
        this.defaultSubject = defaultSubject;
        this.lastEntryUpdate = lastEntryUpdate;
    }

    public VertretungsEntry(int entryId, LocalDateTime registrationTimeStamp, LocalDate representationDate, String className, String defaultHour, String defaultRoom, String defaultTeacher, String defaultSubject) {
        this.entryId = entryId;
        this.registrationTimeStamp = LocalDateTime.parse(registrationTimeStamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        this.representationDate = representationDate;
        this.className = className;
        this.defaultHour = defaultHour;
        this.defaultRoom = defaultRoom;
        this.defaultTeacher = defaultTeacher;
        this.defaultSubject = defaultSubject;
    }

    public VertretungsEntry(LocalDateTime registrationTimeStamp, LocalDate representationDate, String className, String defaultHour, String defaultRoom, String defaultTeacher, String defaultSubject) {
        this.registrationTimeStamp = LocalDateTime.parse(registrationTimeStamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        this.representationDate = representationDate;
        this.className = className;
        this.defaultHour = defaultHour;
        this.defaultRoom = defaultRoom;
        this.defaultTeacher = defaultTeacher;
        this.defaultSubject = defaultSubject;
    }

    //Just use on insert....
    public void insertAllNewUpdates() {
        //load entryId
        //TODO: Check if it's working fine....
        entryId = SQLManager.getLastInsertedEntryId();
        System.out.print("Id: "+entryId);
/*        if(getNote() != null) SQLManager.insertNewUpdate(UpdateType.NOTE, entryId, note);
        if(getNewHour() != null) SQLManager.insertNewUpdate(UpdateType.HOUR, entryId, newHour);
        if(getNewRoom() != null) SQLManager.insertNewUpdate(UpdateType.ROOM, entryId, newRoom);
        if(getNewTeacher() != null) SQLManager.insertNewUpdate(UpdateType.TEACHER, entryId, getNewTeacherShort()+"__"+getNewTeacherLong());
        if(getNewSubject() != null) SQLManager.insertNewUpdate(UpdateType.SUBJECT, entryId, newSubject);*/

    }

    public int getEntryId() {
        return entryId;
    }

    public LocalDateTime getRegistrationTimeStamp() {
        return registrationTimeStamp;
    }

    public LocalDate getRepresentationDate() {
        return representationDate;
    }

    public String getClassName() {
        if (className == null || className.equals("") || className.equals(" ") || className.equals("..") || className.equalsIgnoreCase("null")){
            return "";
        }
        return className;
    }

    public String getDefaultHour() {
        if (defaultHour == null || defaultHour.equals("") || defaultHour.equals(" ") || defaultHour.equals("..") || defaultHour.equalsIgnoreCase("null")){
            return "";
        }
        return defaultHour;
    }

    public String getDefaultRoom() {
        if (defaultRoom == null || defaultRoom.equals("") || defaultRoom.equals(" ") || defaultRoom.equals("..") || defaultRoom.equalsIgnoreCase("null")){
            return "";
        }
        return defaultRoom;
    }

    public String getDefaultTeacherShort() {
        return defaultTeacher.split(" ")[0];
    }

    public String getDefaultTeacherLong() {
        return defaultTeacher.split(" ")[1].replace("(", "")+" "+defaultTeacher.split(" ")[2].replace(")", "");
    }
    public String getDefaultSubject() {
        return defaultSubject;
    }


    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public VertretungsEntryUpdate getLastEntryUpdate() {
        return lastEntryUpdate;
    }

    public void setLastEntryUpdate(VertretungsEntryUpdate lastEntryUpdate) {
        this.lastEntryUpdate = lastEntryUpdate;
    }

    /* NOT Up to date, I think
0 means same
1 absolut different
2 defaults same
             */

    @Override
    public int compareTo(@NotNull VertretungsEntry o) {
        boolean defaultSame = getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .equals(o.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) &&
                getClassName().replace(" ", "").equals(o.getClassName().replace(" ", "")) &&
                getDefaultHour().replace(" ", "").equals(o.getDefaultHour().replace(" ", "")) &&
                getDefaultRoom().replace(" ", "").equals(o.getDefaultRoom().replace(" ", ""));
        //DEBUG STUFF

        if (defaultSame) {/*
            System.out.println("------------------------");
            System.out.println("New:"+toString());
            System.out.println("Old:"+o.toString());
            System.out.println("------------------------");*/
            if(getLastEntryUpdate() != null && o.getLastEntryUpdate() != null && getLastEntryUpdate().compareTo(o.getLastEntryUpdate()) != 0){
                return 2;
            }
            return 0;
        } else return 1;
    }

    @Override
    public String toString() {
        return "VertretungsEntry{" +
                "entryId=" + entryId +
                ", registrationTimeStamp=" + registrationTimeStamp +
                ", representationDate=" + representationDate +
                ", className='" + className + '\'' +
                ", defaultHour='" + defaultHour + '\'' +
                ", defaultRoom='" + defaultRoom + '\'' +
                ", defaultTeacherShort='" + getDefaultTeacherShort() + '\'' +
                ", defaultSubject='" + defaultSubject + '\'' +
                '}';
    }
    public String toStringDefaults(){
        return "VertretungsEntry{" +
                ", representationDate=" + representationDate +
                ", className='" + className + '\'' +
                ", defaultHour='" + defaultHour + '\'' +
                ", defaultRoom='" + defaultRoom + '\'' +
                ", defaultTeacher='" + defaultTeacher + '\'' +
                ", defaultSubject='" + defaultSubject + '\'';
    }

    public String getSendUpdateMessage(){
        return getSendUpdateMessage("Neuer Eintrag");
    }

    public String getSendUpdateMessage(String reason){
        StringBuilder builder = new StringBuilder();
        builder.append(reason+" für den ")
                .append(getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .append("\n");

        builder.append("Klasse: ").append(getClassName()).append("\n");
        builder.append("Stunde: ").append(getDefaultHour()).append("\n");
        builder.append("Fach: ").append(getDefaultSubject()).append("\n");
        if(getLastEntryUpdate().getNote() != null && !getLastEntryUpdate().getNote().equals("null"))
            builder.append("Anmerkung: ").append(getLastEntryUpdate().getNote()).append("\n");
        if(getLastEntryUpdate().getTeacherLong() != null && !getLastEntryUpdate().getTeacherLong().equals("null"))
            builder.append("Vertreter: ").append(getLastEntryUpdate().getTeacherLong()).append("\n");
        if(getLastEntryUpdate().getSubject() != null && !getLastEntryUpdate().getSubject().equals("null"))
            builder.append("Neues Fach: ").append(getLastEntryUpdate().getSubject()).append("\n");
        if(getLastEntryUpdate().getRoom() != null && !getLastEntryUpdate().getRoom().equals("null"))
            builder.append("Neuer Raum: ").append(getLastEntryUpdate().getRoom()).append("\n");
        if(getLastEntryUpdate().getHour() != null && !getLastEntryUpdate().getHour().equals("null"))
            builder.append("Neue Stunde: ").append(getLastEntryUpdate().getHour()).append("\n");
        return builder.toString();
    }

}
