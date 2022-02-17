package de.cypix.vertretungsplanbot.vertretungsplan;

import java.time.LocalDateTime;

public class VertretungsEntry {
    private int entryId;
    private LocalDateTime registrationTimeStamp;
    private LocalDateTime lastRefreshTimeStamp;
    private LocalDateTime representationDate;
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

    public VertretungsEntry(int entryId, LocalDateTime registrationTimeStamp, LocalDateTime lastRefreshTimeStamp, LocalDateTime representationDate, String className, String defaultHour, String defaultRoom, String defaultTeacher, String defaultSubject, String note, String newHour, String newRoom, String newTeacher, String newSubject) {
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

    public int getEntryId() {
        return entryId;
    }

    public LocalDateTime getRegistrationTimeStamp() {
        return registrationTimeStamp;
    }

    public LocalDateTime getLastRefreshTimeStamp() {
        return lastRefreshTimeStamp;
    }

    public LocalDateTime getRepresentationDate() {
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
}
