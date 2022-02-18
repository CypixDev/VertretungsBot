package de.cypix.vertretungsplanbot.vertretungsplan;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VertretungsEntry implements Comparable<VertretungsEntry>{
    private int entryId;
    private LocalDateTime registrationTimeStamp;
    private LocalDateTime lastRefreshTimeStamp;
    private LocalDate representationDate;
    private String className;
    private String defaultHour;
    private String defaultRoom;
    private String defaultTeacher;
    private String defaultSubject;
    private String note;
    private String newHour;
    private String newRoom;
    private String newTeacher;
    private String newSubject;

    public VertretungsEntry(int entryId, LocalDateTime registrationTimeStamp, LocalDateTime lastRefreshTimeStamp, LocalDate representationDate, String className, String defaultHour, String defaultRoom, String defaultTeacher, String defaultSubject, String note, String newHour, String newRoom, String newTeacher, String newSubject) {
        this.entryId = entryId;
        this.registrationTimeStamp = registrationTimeStamp;
        this.lastRefreshTimeStamp = lastRefreshTimeStamp;
        this.representationDate = representationDate;
        this.className = className;
        this.defaultHour = defaultHour;
        this.defaultRoom = defaultRoom;
        this.defaultTeacher = defaultTeacher;
        this.defaultSubject = defaultSubject;
        this.note = note;
        this.newHour = newHour;
        this.newRoom = newRoom;
        this.newTeacher = newTeacher;
        this.newSubject = newSubject;
    }

    public VertretungsEntry(LocalDateTime registrationTimeStamp, LocalDateTime lastRefreshTimeStamp, LocalDate representationDate, String className, String defaultHour, String defaultRoom, String defaultTeacher, String defaultSubject, String note, String newHour, String newRoom, String newTeacher, String newSubject) {
        this.registrationTimeStamp = registrationTimeStamp;
        this.lastRefreshTimeStamp = lastRefreshTimeStamp;
        this.representationDate = representationDate;
        this.className = className;
        this.defaultHour = defaultHour;
        this.defaultRoom = defaultRoom;
        this.defaultTeacher = defaultTeacher;
        this.defaultSubject = defaultSubject;
        this.note = note;
        this.newHour = newHour;
        this.newRoom = newRoom;
        this.newTeacher = newTeacher;
        this.newSubject = newSubject;
    }

    public VertretungsEntry(LocalDateTime registrationTimeStamp, LocalDateTime lastRefreshTimeStamp, LocalDate representationDate, String className, String defaultHour, String defaultRoom, String defaultTeacher, String defaultSubject, String note) {
        this.registrationTimeStamp = registrationTimeStamp;
        this.lastRefreshTimeStamp = lastRefreshTimeStamp;
        this.representationDate = representationDate;
        this.className = className;
        this.defaultHour = defaultHour;
        this.defaultRoom = defaultRoom;
        this.defaultTeacher = defaultTeacher;
        this.defaultSubject = defaultSubject;
        this.note = note;
    }

    public int getEntryId() {
        return entryId;
    }

    public LocalDateTime getRegistrationTimeStamp() {
        return registrationTimeStamp;
    }

    public LocalDateTime getLastRefreshTimeStamp() {
        return lastRefreshTimeStamp;
    }

    public LocalDate getRepresentationDate() {
        return representationDate;
    }

    public String getClassName() {
        return className;
    }

    public String getDefaultHour() {
        return defaultHour;
    }

    public String getDefaultRoom() {
        return defaultRoom;
    }

    public String getDefaultTeacher() {
        return defaultTeacher;
    }

    public String getDefaultSubject() {
        return defaultSubject;
    }

    public String getNote() {
        return note;
    }

    public String getNewHour() {
        return newHour;
    }

    public String getNewRoom() {
        return newRoom;
    }

    public String getNewTeacher() {
        return newTeacher;
    }

    public String getNewSubject() {
        return newSubject;
    }


    /*
    -1 means different
    0 means same
    1 means default is same
     */
    @Override
    public int compareTo(@NotNull VertretungsEntry o) {
        boolean defaultSame = false;
        //Hopefully unique identification
        if(getRepresentationDate().isEqual(o.getRepresentationDate())&&
                getClassName().equals(o.getClassName())&&
                getDefaultHour().equals(o.getDefaultHour())&&
                getDefaultRoom().equals(o.getDefaultRoom()))
            defaultSame = true;
        if(defaultSame && getNote().equals(o.getNote())&&
                getNewHour().equals(o.getNewHour())&&
                getNewRoom().equals(o.getNewRoom())&&
                getNewTeacher().equals(o.getNewTeacher())&&
                getNewSubject().equals(o.getNewSubject())){
            return 0;
        }else if(defaultSame){
            return 2;
        }else return 1;
    }
}
