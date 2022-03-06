package de.cypix.vertretungsplanbot.sql;

public enum UpdateType {

    NOTE("entry_update_note", "note_id", "note_id", "entry_note", "note_name"),
    HOUR("entry_update_hour", "hour_id", "hour_id", "entry_hour", "hour_name"),
    ROOM("entry_update_room", "room_id", "room_id", "entry_room", "room_name"),
    TEACHER("entry_update_teacher", "teacher_id", "teacher_id", "entry_teacher", "teacher_name"),
    SUBJECT("entry_update_subject", "subject_id", "subject_id", "entry_subject", "subject_name");

    final String tableName;
    final String columnIdName;
    final String entryColumnId;
    final String entryTableName;
    final String entryColumnIdName;


    UpdateType(String tableName, String columnIdName, String entryColumnId, String entryTableName, String entryColumnIdName) {
        this.tableName = tableName;
        this.columnIdName = columnIdName;
        this.entryColumnId = entryColumnId;
        this.entryTableName = entryTableName;
        this.entryColumnIdName = entryColumnIdName;
    }
}
