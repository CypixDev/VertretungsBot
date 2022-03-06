package de.cypix.vertretungsplanbot.vertretungsplan;

import de.cypix.vertretungsplanbot.sql.UpdateType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VertretungsEntryUpdate implements Comparable<VertretungsEntryUpdate> {

    private VertretungsEntry mainEntry;
    private int updateId;
    private LocalDateTime registrationDateTime;
    private String note;
    private String hour;
    private String room;
    private String teacherLong;
    private String teacherShort;
    private String subject;

    public VertretungsEntryUpdate(VertretungsEntry mainEntry, LocalDateTime registrationDateTime) {
        this.mainEntry = mainEntry;
        this.registrationDateTime = registrationDateTime;
    }

    public VertretungsEntry getMainEntry() {
        return mainEntry;
    }

    public void setMainEntry(VertretungsEntry mainEntry) {
        this.mainEntry = mainEntry;
    }

    public int getUpdateId() {
        return updateId;
    }

    public void setUpdateId(int updateId) {
        this.updateId = updateId;
    }

    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public String getNote() {
        if (note == null || note.equals("") || note.equals(" ") || note.equals("..") || note.equalsIgnoreCase("null")){
            return null;
        }
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHour() {
        if (hour == null || hour.equals("") || hour.equals(" ") || hour.equals("..") || hour.equalsIgnoreCase("null")){
            return null;
        }
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getRoom() {
        if (room == null || room.equals("") || room.equals(" ") || room.equals("..") || room.equalsIgnoreCase("null")){
            return null;
        }
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
    public String getTeacherShort() {
        if (teacherShort == null || teacherShort.equals("") || teacherShort.equals(" ") || teacherShort.equals("..")|| teacherShort.equalsIgnoreCase("null")) return null;
        return teacherShort;
    }

    public String getTeacherLong() {
        if (teacherLong == null || teacherLong.equals("") || teacherLong.equals(" ") || teacherLong.equals("..")|| teacherLong.equalsIgnoreCase("null")) return null;
        return teacherLong;
    }

    public void setTeacherLong(String teacherLong) {
        this.teacherLong = teacherLong;
    }

    public void setTeacherShort(String teacherShort) {
        this.teacherShort = teacherShort;
    }

    public String getSubject() {
        if (subject == null || subject.equals("") || subject.equals(" ") || subject.equals("..") || subject.equalsIgnoreCase("null")){
            return null;
        }
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int compareTo(@NotNull VertretungsEntryUpdate o) {
/*        System.out.println("------------------------");
        System.out.println("New:"+toString());
        System.out.println("Old:"+o.toString());*/
        if (getNote() != null && o.getNote() != null){
            if(!getNote().equals(o.getNote())) return 2;
        }
        if (getHour() != null && o.getHour() != null){
            if(!getHour().equals(o.getHour())) return 2;
        }
        if (getRoom() != null && o.getRoom() != null){
            if(!getRoom().equals(o.getRoom())) return 2;
        }
        if (getTeacherShort() != null && o.getTeacherShort() != null) {
            if (!getTeacherShort().equals(o.getTeacherShort())) return 2;
        }
        if (getSubject() != null && o.getSubject() != null){
            if(!getSubject().equals(o.getSubject())) return 2;
        }/*
        System.out.println("SAME");
        System.out.println("------------------------");*/
        return 0;
    }

    public List<UpdateType> getUpdateTypeList() {
        List<UpdateType> list = new ArrayList<>();
        if(getNote() != null) list.add(UpdateType.NOTE);
        if(getHour() != null) list.add(UpdateType.HOUR);
        if(getRoom() != null) list.add(UpdateType.ROOM);
        if(getTeacherLong() != null) list.add(UpdateType.TEACHER);
        if(getSubject() != null) list.add(UpdateType.SUBJECT);

        return list;
    }

    public String getValue(UpdateType type) {
        String value = "";
        switch (type){
            case NOTE -> value = getNote();
            case HOUR -> value = getHour();
            case ROOM -> value = getRoom();
            case TEACHER -> value = getTeacherShort()+"__"+getTeacherLong();
            case SUBJECT -> value = getSubject();
        }
        return value;
    }


    @Override
    public String toString() {
        return "VertretungsEntryUpdate{" +
                "mainEntry=" + mainEntry +
                ", updateId=" + updateId +
                ", registrationDateTime=" + registrationDateTime +
                ", note='" + note + '\'' +
                ", hour='" + hour + '\'' +
                ", room='" + room + '\'' +
                ", teacherLong='" + teacherLong + '\'' +
                ", teacherShort='" + teacherShort + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
