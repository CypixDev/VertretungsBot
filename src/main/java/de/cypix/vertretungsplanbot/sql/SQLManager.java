package de.cypix.vertretungsplanbot.sql;

import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.remind.Remind;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntryUpdate;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    private static final Logger logger = Logger.getLogger(SQLManager.class);


    public static void insertNewEntry(VertretungsEntry entry) {

        StringBuilder query = new StringBuilder("INSERT INTO entry(");
        query.append("registration_timestamp_id,");
        query.append("representation_date_id,");
        query.append("class_id,");
        query.append("default_hour_id,");
        query.append("default_room_id,");
        query.append("default_teacher_id,");
        query.append("default_subject_id");

        query.append(") VALUES (");

        if (!SQLManager.exists("entry_timestamp",
                "timestamp_time",
                entry.getRegistrationTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
            SQLManager.insertNewTimeStamp(entry.getRegistrationTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        query.append("(SELECT timestamp_id FROM entry_timestamp WHERE timestamp_time='")
                .append(entry.getRegistrationTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("'), ");


        if (!SQLManager.exists("entry_timestamp",
                "timestamp_time",
                entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
            SQLManager.insertNewTimeStamp(entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        query.append("(SELECT timestamp_id FROM entry_timestamp WHERE timestamp_time='")
                .append(entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("'), ");


        if (!SQLManager.exists("entry_class", "class_name", entry.getClassName()))
            SQLManager.insertNewClass(entry.getClassName());
        query.append("(SELECT class_id FROM entry_class WHERE class_name='").append(entry.getClassName()).append("'), ");

        if (!SQLManager.exists("entry_hour", "hour_name", entry.getDefaultHour()))
            SQLManager.insertNewHour(entry.getDefaultHour());
        query.append("(SELECT hour_id FROM entry_hour WHERE hour_name='").append(entry.getDefaultHour()).append("'), ");

        if (!SQLManager.exists("entry_room", "room_name", entry.getDefaultRoom()))
            SQLManager.insertNewRoom(entry.getDefaultRoom());
        query.append("(SELECT room_id FROM entry_room WHERE room_name='").append(entry.getDefaultRoom()).append("'), ");

        if (!SQLManager.exists("entry_teacher", "teacher_name", entry.getDefaultTeacherLong()))
            SQLManager.insertNewTeacher(entry.getDefaultTeacherShort(), entry.getDefaultTeacherLong());
        query.append("(SELECT teacher_id FROM entry_teacher WHERE teacher_name='").append(entry.getDefaultTeacherLong()).append("'), ");


        if (!SQLManager.exists("entry_subject", "subject_name", entry.getDefaultSubject()))
            SQLManager.insertNewSubject(entry.getDefaultSubject());
        query.append("(SELECT subject_id FROM entry_subject WHERE subject_name='").append(entry.getDefaultSubject()).append("'))");

        VertretungsPlanBot.getSqlConnector().executeUpdate(query.toString());
        insertNewUpdate(getLastInsertedEntryId(), entry.getLastEntryUpdate());
    }

    public static int getLastInsertedEntryId(){
        ResultSet set = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT LAST_INSERT_ID(entry_id) AS entry_id FROM entry ORDER BY entry_id DESC LIMIT 1;");
        try {
            if(set.next()) return set.getInt("entry_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void insertNewNote(String note) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry_note(note_name) VALUES ('" + note + "')");
    }

    private static void insertNewClass(String className) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry_class(class_name) VALUES ('" + className + "')");
    }

    private static void insertNewRoom(String roomName) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry_room(room_name) VALUES ('" + roomName + "')");
    }

    private static void insertNewHour(String hourName) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry_hour(hour_name) VALUES ('" + hourName + "')");
    }

    private static void insertNewTeacher(String teacherShort, String teacherName) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry_teacher(teacher_short, teacher_name) VALUES ('" + teacherShort + "', '" + teacherName + "')");
    }

    private static void insertNewSubject(String subjectName) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry_subject(subject_name) VALUES ('" + subjectName + "')");
    }

    private static void insertNewTimeStamp(String timeStamp) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry_timestamp(timestamp_time) VALUES ('" + timeStamp + "')");
    }

    @Deprecated
    public static void updateEntry(VertretungsEntry entry) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("UPDATE entry SET note='" +/* entry.getNote() +
                "', new_hour='" + entry.getNewHour() +
                "', new_room='" + entry.getNewRoom() +
                "', new_teacher='" + entry.getNewTeacher() +
                "', new_subject='" + entry.getNewSubject() + "'" +*/
                "WHERE " +
                "representation_date='" + entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' " +
                "AND " +
                "class='" + entry.getClassName() + "' " +
                "AND " +
                "default_hour='" + entry.getDefaultHour() + "';");
    }

    public static void insetNewUpdate(UpdateType type, VertretungsEntry entry){
        String value = "";
        switch (type){
/*            case NOTE -> value = entry.getNote();
            case HOUR -> value = entry.getNewHour();
            case ROOM -> value = entry.getNewRoom();
            case TEACHER -> value = entry.getNewTeacher();
            case SUBJECT -> value = entry.getNewSubject();*/
        }
        //Just normal check for nor bad info
        if(value == null || value.equals("") || value.equals(" ")) return;

        insertNewUpdate(type, entry.getEntryId(), value);
    }

    public static void insertNewUpdate(int entryId, VertretungsEntryUpdate entryUpdate){
        StringBuilder str = new StringBuilder("INSERT INTO entry_update(entry_id, registration_timestamp_id) VALUES (");
        str.append(entryId).append(", ");
        if (!SQLManager.exists("entry_timestamp", "timestamp_time",
                entryUpdate.getRegistrationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
            SQLManager.insertNewTimeStamp(entryUpdate.getRegistrationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        str.append("(SELECT timestamp_id FROM entry_timestamp WHERE timestamp_time='")
                .append(entryUpdate.getRegistrationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("'))");
        VertretungsPlanBot.getSqlConnector().executeUpdate(str.toString());
        entryUpdate.setUpdateId(getNewestEntryUpdateId(entryId));

        for (UpdateType type : entryUpdate.getUpdateTypeList()) {
            String value = entryUpdate.getValue(type);
            StringBuilder query = new StringBuilder("INSERT INTO ");
            query.append(type.tableName);
            query.append("(entry_update_id, ");
            query.append(type.entryColumnId);
            query.append(")");
            query.append(" VALUES ");
            query.append("(");
            query.append(entryUpdate.getUpdateId());
            query.append(", ");
            if (!SQLManager.exists(type.entryTableName, type.entryColumnIdName, type == UpdateType.TEACHER ? value.split("__")[1] : value))
                switch (type) {
                    case NOTE -> SQLManager.insertNewNote(value);
                    case HOUR -> SQLManager.insertNewHour(value);
                    case ROOM -> SQLManager.insertNewRoom(value);
                    case TEACHER -> SQLManager.insertNewTeacher(value.split("__")[0], value.split("__")[1]);
                    case SUBJECT -> SQLManager.insertNewSubject(value);
                }
            query.append("(SELECT ")
                    .append(type.columnIdName)
                    .append(" FROM ")
                    .append(type.entryTableName)
                    .append(" WHERE ")
                    .append(type.entryColumnIdName).append("='")
                    .append(type == UpdateType.TEACHER ? value.split("__")[1] : value).append("'))");
            VertretungsPlanBot.getSqlConnector().executeUpdate(query.toString());
        }
    }

    private static int getNewestEntryUpdateId(int entryId) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT entry_update_id FROM entry_update WHERE entry_id="+entryId+" " +
                "ORDER BY registration_timestamp_id DESC LIMIT 1");
        try {
            if(rs.next()) return rs.getInt("entry_update_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Deprecated
    public static void insertNewUpdate(UpdateType type, int entryId, String value){
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(type.tableName);
        query.append("(registration_timestamp_id, entry_id, ");
        query.append(type.entryColumnId);
        query.append(")");
        query.append(" VALUES ");
        query.append("(");
        if (!SQLManager.exists("entry_timestamp",
                "timestamp_time",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
            SQLManager.insertNewTimeStamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        query.append("(SELECT timestamp_id FROM entry_timestamp WHERE timestamp_time='")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("'), ");
        query.append(entryId);
        query.append(", ");
        if (!SQLManager.exists(type.entryTableName, type.entryColumnIdName, type == UpdateType.TEACHER ? value.split("__")[1] : value))
            switch (type) {
                case NOTE -> SQLManager.insertNewNote(value);
                case HOUR -> SQLManager.insertNewHour(value);
                case ROOM -> SQLManager.insertNewRoom(value);
                case TEACHER -> SQLManager.insertNewTeacher(value.split("__")[0], value.split("__")[1]);
                case SUBJECT -> SQLManager.insertNewSubject(value);
            }
        query.append("(SELECT ")
                .append(type.columnIdName)
                .append(" FROM ")
                .append(type.entryTableName)
                .append(" WHERE ")
                .append(type.entryColumnIdName).append("='")
                .append(type == UpdateType.TEACHER ? value.split("__")[1] : value).append("'))");
        VertretungsPlanBot.getSqlConnector().executeUpdate(query.toString());

    }

    public static List<VertretungsEntry> getAllRelevantEntries() {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet(
                "SELECT entry.entry_id AS entry_id, register_datetime.timestamp_time AS registration_timestamp, " +
                        "representation_date.timestamp_time AS representation_date, entry_class.class_name AS class, default_hour.hour_name AS default_hour, " +
                        "default_room.room_name AS default_room, default_teacher.teacher_name AS default_teacher_long, default_teacher.teacher_short AS default_teacher_short, default_subject.subject_name AS default_subject " +
                        "FROM entry " +
                        "" +
                        "LEFT JOIN entry_timestamp AS register_datetime ON entry.registration_timestamp_id = register_datetime.timestamp_id " +
                        "LEFT JOIN entry_timestamp AS representation_date ON entry.representation_date_id = representation_date.timestamp_id " +
                        "" +
                        "LEFT JOIN entry_class ON entry.class_id = entry_class.class_id " +
                        "" +
                        "LEFT JOIN entry_hour AS default_hour ON entry.default_hour_id = default_hour.hour_id " +
                        "LEFT JOIN entry_room AS default_room ON entry.default_room_id = default_room.room_id " +
                        "LEFT JOIN entry_teacher AS default_teacher ON entry.default_teacher_id = default_teacher.teacher_id " +
                        "LEFT JOIN entry_subject AS default_subject ON entry.default_subject_id = default_subject.subject_id " +
                        "" +
                        "WHERE representation_date.timestamp_time >= CURRENT_DATE " +
                        "GROUP BY entry.entry_id; "
        );
        return getVertretungsEntries(rs);
    }

    public static VertretungsEntryUpdate getLastVertretungsEntryUpdate(VertretungsEntry entry){
        if(entry.getEntryId() == 0) return null;
        //TODO: select just what is needed!
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry_update " +
                "LEFT JOIN entry_timestamp AS register_datetime ON entry_update.registration_timestamp_id = register_datetime.timestamp_id " +

                "LEFT JOIN entry_update_note ON entry_update.entry_update_id = entry_update_note.entry_update_id " +
                "LEFT JOIN entry_note ON entry_note.note_id = entry_update_note.note_id " +

                "LEFT JOIN entry_update_hour ON entry_update.entry_update_id = entry_update_hour.entry_update_id " +
                "LEFT JOIN entry_hour ON entry_hour.hour_id = entry_update_hour.hour_id " +

                "LEFT JOIN entry_update_room ON entry_update.entry_update_id = entry_update_room.entry_update_id " +
                "LEFT JOIN entry_room ON entry_room.room_id = entry_update_room.room_id " +

                "LEFT JOIN entry_update_teacher ON entry_update.entry_update_id = entry_update_teacher.entry_update_id " +
                "LEFT JOIN entry_teacher ON entry_teacher.teacher_id = entry_update_teacher.teacher_id " +

                "LEFT JOIN entry_update_subject ON entry_update.entry_update_id = entry_update_subject.entry_update_id " +
                "LEFT JOIN entry_subject ON entry_subject.subject_id = entry_update_subject.subject_id " +

                "WHERE entry_id = "+entry.getEntryId()+" " +
                "ORDER BY registration_timestamp_id DESC LIMIT 1");
        try{
            while (rs.next()) {
                //TODO: locally save vars or verify they are saved locally - Working for testing...
                VertretungsEntryUpdate entryUpdate = new VertretungsEntryUpdate(entry, rs.getTimestamp("register_datetime.timestamp_time").toLocalDateTime());
                if(rs.getString("entry_note.note_name") != null && !rs.getString("entry_note.note_name").equalsIgnoreCase("null")) entryUpdate.setNote(rs.getString("entry_note.note_name"));
                if(rs.getString("entry_hour.hour_name") != null && !rs.getString("entry_hour.hour_name").equalsIgnoreCase("null")) entryUpdate.setHour(rs.getString("entry_hour.hour_name"));
                if(rs.getString("entry_room.room_name") != null && !rs.getString("entry_room.room_name").equalsIgnoreCase("null")) entryUpdate.setRoom(rs.getString("entry_room.room_name"));
                if(rs.getString("entry_teacher.teacher_name") != null && !rs.getString("entry_teacher.teacher_name").equalsIgnoreCase("null")) entryUpdate.setTeacherLong(rs.getString("entry_teacher.teacher_name"));
                if(rs.getString("entry_teacher.teacher_short") != null && !rs.getString("entry_teacher.teacher_short").equalsIgnoreCase("null")) entryUpdate.setTeacherShort(rs.getString("entry_teacher.teacher_short"));
                if(rs.getString("entry_subject.subject_name") != null && !rs.getString("entry_subject.subject_name").equalsIgnoreCase("null")) entryUpdate.setSubject(rs.getString("entry_subject.subject_name"));
                return (entryUpdate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getAllData(long chatId) {
        List<String> list = new ArrayList<>();
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM user WHERE chat_id=" + chatId);
        try {
            if (rs != null && rs.next()) {
                list.add("Vorname: " + rs.getString("user_first_name"));
                list.add("Nachname: " + rs.getString("user_last_name"));
                list.add("Username: " + rs.getString("user_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.add("Alle notifications:");
        for (String s : getAllNotifiesByChatId(chatId)) {
            list.add("- " + s);
        }
        return list;
    }

    public static void deleteNotification(long chatId, String className) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("DELETE FROM notification WHERE user_id=(SELECT user_id FROM user WHERE chat_id=" + chatId + ") AND class='" + className + "';");
    }

    @NotNull
    private static List<VertretungsEntry> getVertretungsEntries(ResultSet rs) {
        List<VertretungsEntry> list = new ArrayList<>();
        try {
            while (rs.next()) {
                VertretungsEntry entry = new VertretungsEntry(
                        rs.getInt("entry_id"),
                        rs.getTimestamp("registration_timestamp").toLocalDateTime(),
                        rs.getTimestamp("representation_date").toLocalDateTime().toLocalDate(),
                        rs.getString("class"),
                        rs.getString("default_hour"),
                        rs.getString("default_room"),
                        rs.getString("default_teacher_short")+" ("+rs.getString("default_teacher_long")+")",
                        rs.getString("default_subject"));
                entry.setLastEntryUpdate(getLastVertretungsEntryUpdate(entry));
                list.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<VertretungsEntry> getAllEntries() {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry LIMIT 20");
        return getVertretungsEntries(rs);
    }

    public static VertretungsEntry getEntry(int entryId) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry WHERE entry_id=" + entryId);
        try {
            while (rs.next()) {
                return (new VertretungsEntry(
                        rs.getInt("entry_id"),
                        rs.getTimestamp("registration_timestamp").toLocalDateTime(),
                        rs.getTimestamp("representation_date").toLocalDateTime().toLocalDate(),
                        rs.getString("class"),
                        rs.getString("default_hour"),
                        rs.getString("default_room"),
                        rs.getString("default_teacher"),
                        rs.getString("default_subject")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated //Not checked....
    public static LocalDateTime getLastRegisteredRefresh() {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT last_refresh_timestamp FROM entry ORDER BY last_refresh_timestamp DESC LIMIT 1");
        try {
            if (rs != null) {
                if (rs.next()) {
                    return rs.getTimestamp("last_refresh_timestamp").toLocalDateTime();
                }
            }
        } catch (SQLException e) {
            logger.info("Returning now-1day because no entries...");
            return LocalDateTime.now().minusDays(1);
        }
        return LocalDateTime.now().minusDays(1);
    }


    public static boolean exists(String table, String column, String value) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM " + table + " WHERE " + column + "='" + value + "'");
        try {
            if (rs.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void insertNewUser(Long chatId, String firstName, String lastName) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO user(" +
                "chat_id," +
                "user_first_name," +
                "user_last_name" +
                ") VALUES (" +
                chatId + ", '" +
                firstName + "', '" +
                lastName + "')");
    }

    public static void insertNewNotification(Long chatId, String className) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO notification(user_id, class) " +
                "VALUES " +
                "((SELECT user_id FROM user WHERE chat_id=" + chatId + ") , '" + className + "');");
    }

    public static List<Long> getAllChatIDsByNotifyClass(String className) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT chat_id FROM notification" +
                " INNER JOIN user ON notification.user_id = user.user_id" +
                " WHERE class='" + className + "'");
        List<Long> list = new ArrayList<>();
        try {
            while (rs.next()) {
                list.add(rs.getLong("chat_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> getAllNotifiesByUserId(int userId) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT class FROM notification" +
                " WHERE user_id=" + userId);
        List<String> list = new ArrayList<>();
        try {
            while (rs.next()) {
                list.add(rs.getString("class"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> getAllNotifiesByChatId(long chatId) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT class FROM notification" +
                " INNER JOIN user ON notification.user_id = user.user_id" +
                " WHERE chat_id =" + chatId);
        List<String> list = new ArrayList<>();
        try {
            while (rs.next()) {
                list.add(rs.getString("class"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isRegistered(long chatId) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT user_id FROM user WHERE chat_id=" + chatId);
        try {
            if (rs.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<VertretungsEntry> getAllRelevantEntriesByClass(String className) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet(
                "SELECT entry.entry_id AS entry_id, register_datetime.timestamp_time AS registration_timestamp, " +
                "representation_date.timestamp_time AS representation_date, entry_class.class_name AS class, default_hour.hour_name AS default_hour, " +
                "default_room.room_name AS default_room, default_teacher.teacher_name AS default_teacher_long, default_teacher.teacher_short AS default_teacher_short, default_subject.subject_name AS default_subject " +
                "FROM entry " +
                "" +
                "LEFT JOIN entry_timestamp AS register_datetime ON entry.registration_timestamp_id = register_datetime.timestamp_id " +
                "LEFT JOIN entry_timestamp AS representation_date ON entry.representation_date_id = representation_date.timestamp_id " +
                "" +
                "LEFT JOIN entry_class ON entry.class_id = entry_class.class_id " +
                "" +
                "LEFT JOIN entry_hour AS default_hour ON entry.default_hour_id = default_hour.hour_id " +
                "LEFT JOIN entry_room AS default_room ON entry.default_room_id = default_room.room_id " +
                "LEFT JOIN entry_teacher AS default_teacher ON entry.default_teacher_id = default_teacher.teacher_id " +
                "LEFT JOIN entry_subject AS default_subject ON entry.default_subject_id = default_subject.subject_id " +
                "" +
                "WHERE representation_date.timestamp_time >= CURRENT_DATE " +
                        "AND entry.class_id=" + "(SELECT class_id FROM entry_class WHERE class_name='"+className+"')" +
                "GROUP BY entry.entry_id;");
        if(rs == null) return new ArrayList<>();
        return getVertretungsEntries(rs);
    }

    public static boolean insertNewRemind(long chatId, String className, String hour){
        return VertretungsPlanBot.getSqlConnector().executeUpdateWithFeedBack("INSERT INTO remind(notification_id, hour) VALUES (" +
                "(SELECT notification_id FROM notification "+
                "WHERE notification.class='"+className+"' AND user_id = (SELECT user_id FROM user WHERE chat_id="+chatId+")), '" + hour + "')");
    }

    public static boolean insertNewRemind(int notificationId, String hour){
        return VertretungsPlanBot.getSqlConnector().executeUpdateWithFeedBack("INSERT INTO remind(notification_id, hour) VALUES (" +
                notificationId+", '"+hour+"'");
    }

    public static void deleteEverything(Long chatId) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("DELETE FROM user WHERE chat_id = "+chatId);
        logger.info("Deleted all data from "+chatId);
    }

    public static List<String> getAllReminderByClassAndChatId(String className, long chatId) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT hour FROM remind " +
                "INNER JOIN notification ON notification.notification_id = remind.notification_id " +
                "INNER JOIN user ON user.chat_id = "+ chatId+" " +
                "WHERE notification.class = '"+className+"' AND user.user_id = notification.user_id;");
        List<String> list = new ArrayList<>();
        if(rs != null){
            try {
                while (rs.next()) {
                    list.add(rs.getString("hour"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static boolean deleteRemind(Long chatId, String className, String hour) {
         return (VertretungsPlanBot.getSqlConnector().executeUpdateWithFeedBack("DELETE FROM remind WHERE " +
                 "notification_id = (SELECT notification_id FROM notification WHERE user_id = "+"" +
                 "(SELECT user_id FROM user WHERE chat_id="+chatId+") AND class = '"+className+"') AND " +
                 "hour = '"+hour+"';"));
    }

    public static List<Remind> getAllRemindsByTime(String currentTime) {
        List<Remind> list = new ArrayList<>();
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT chat_id, hour, class FROM remind " +
                "LEFT JOIN notification ON notification.notification_id = remind.notification_id " +
                "LEFT JOIN user ON user.user_id = notification.user_id " +
                "WHERE hour='"+currentTime+"' OR hour='T"+currentTime+"'");
        if(rs != null){
            try {
                while (rs.next()) {
                    list.add(new Remind(rs.getLong("chat_id"), rs.getString("class"), rs.getString("hour")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<Long> getAllChatIds() {
        List<Long> list = new ArrayList<>();
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT chat_id FROM user;");
        if(rs != null){
            try {
                while (rs.next()) {
                    list.add(rs.getLong("chat_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void deleteEntry(int entryId) {
        VertretungsPlanBot.getSqlConnector().executeUpdateWithFeedBack("DELETE FROM entry WHERE entry_id="+entryId);
    }
}
