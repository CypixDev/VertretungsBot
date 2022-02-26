package de.cypix.vertretungsplanbot.vertretungsplan;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class VertretungsEntry implements Comparable<VertretungsEntry> {
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
        this.registrationTimeStamp = LocalDateTime.parse(registrationTimeStamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
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
        this.registrationTimeStamp = LocalDateTime.parse(registrationTimeStamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
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
        this.registrationTimeStamp = LocalDateTime.parse(registrationTimeStamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
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
        if (note.equals("") || note.equals(" ") || note.equals("..")) {
            return null;
        }
        return note;
    }

    public String getNewHour() {
        if (newHour.equals("") || newHour.equals(" ") || newHour.equals("..")) {
            return null;
        }
        return newHour;
    }

    public String getNewRoom() {
        if (newRoom.equals("") || newRoom.equals(" ") || newRoom.equals("..")) {
            return null;
        }
        return newRoom;
    }

    public String getNewTeacher() {
        if (newTeacher.equals("") || newTeacher.equals(" ") || newTeacher.equals("..")) {
            return null;
        }
        return newTeacher;
    }

    public String getNewSubject() {
        if (newSubject.equals("") || newSubject.equals(" ") || newSubject.equals("..")) {
            return null;
        }
        return newSubject;
    }


    /*
    -1 means different
    0 means same
    1 means default is same
     */
    @Override
    public int compareTo(@NotNull VertretungsEntry o) {
        boolean defaultSame = getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                .equals(o.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) &&
                getClassName().replace(" ", "").equals(o.getClassName().replace(" ", "")) &&
                getDefaultHour().replace(" ", "").equals(o.getDefaultHour().replace(" ", "")) &&
                getDefaultRoom().replace(" ", "").equals(o.getDefaultRoom().replace(" ", ""));
/*        System.out.println(getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                +" equals "+(o.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))) +"\n && "+
                getClassName().replace(" ", "")+" equals "+(o.getClassName().replace(" ", "")) +"\n && "+
                getDefaultHour().replace(" ", "")+" equals "+(o.getDefaultHour().replace(" ", "")) +"\n && "+
                getDefaultRoom().replace(" ", "")+" equals "+(o.getDefaultRoom().replace(" ", ""))+"====> "+defaultSame+"\n\n");
        *///Hopefully unique identification
        if (defaultSame && (getNote() != null && o.getNote() != null && getNote().equals(o.getNote()))) {
            if (!(getNewHour() != null && o.getNewHour() != null && getNewHour().equals(o.getNewHour())))
                return 2;
            if (!(getNewRoom() != null && o.getNewRoom() != null && getNewRoom().equals(o.getNewRoom())))
                return 2;
            if (!(getNewTeacher() != null && o.getNewTeacher() != null && getNewTeacher().equals(o.getNewTeacher())))
                return 2;
            if (!(getNewSubject() != null && o.getNewSubject() != null && getNewSubject().equals(o.getNewSubject())))
                return 2;

            return 0;
        } else if (defaultSame) {
            return 2;
        } else return 1;
    }

    @Override
    public String toString() {
        return "VertretungsEntry{" +
                "entryId=" + entryId +
                ", registrationTimeStamp=" + registrationTimeStamp +
                ", lastRefreshTimeStamp=" + lastRefreshTimeStamp +
                ", representationDate=" + representationDate +
                ", className='" + className + '\'' +
                ", defaultHour='" + defaultHour + '\'' +
                ", defaultRoom='" + defaultRoom + '\'' +
                ", defaultTeacher='" + defaultTeacher + '\'' +
                ", defaultSubject='" + defaultSubject + '\'' +
                ", note='" + note + '\'' +
                ", newHour='" + newHour + '\'' +
                ", newRoom='" + newRoom + '\'' +
                ", newTeacher='" + newTeacher + '\'' +
                ", newSubject='" + newSubject + '\'' +
                '}';
    }
}
