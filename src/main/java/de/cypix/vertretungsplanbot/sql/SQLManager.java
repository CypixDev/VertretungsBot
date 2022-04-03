package de.cypix.vertretungsplanbot.sql;

import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.remind.Remind;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntryUpdate;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
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

        int lastInsertedId = -1;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT LAST_INSERT_ID(entry_id) AS entry_id FROM entry ORDER BY entry_id DESC LIMIT 1;");
                try {
                    if(resultSet.next())
                        lastInsertedId = resultSet.getInt("entry_id");
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return lastInsertedId;
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

    public static String getNameByChatId(long chatId){
        String name = null;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT user_first_name FROM user WHERE chat_id="+chatId);
                try {
                    if(resultSet.next())
                        name = resultSet.getString("user_first_name");
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return name;
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
        int newestEntryUpdateId = -1;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT entry_update_id FROM entry_update WHERE entry_id="+entryId+
                        " ORDER BY registration_timestamp_id DESC LIMIT 1");
                try {
                    if(resultSet.next())
                        newestEntryUpdateId = resultSet.getInt("entry_update_id");
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return newestEntryUpdateId;
    }
    public static LocalDateTime getNewestEntryUpdateTimestamp(int entryId) {
        LocalDateTime newestEntryUpdateTimestamp = null;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT entry_timestamp.timestamp_time FROM entry_update" +
                        " LEFT JOIN entry_timestamp ON entry_timestamp.id = entry_update.registration_timestamp_id" +
                        " WHERE entry_id="+entryId+
                        " ORDER BY registration_timestamp_id DESC LIMIT 1");
                try {
                    if(resultSet.next())
                        newestEntryUpdateTimestamp = resultSet.getTimestamp("entry_timestamp.timestamp_time").toLocalDateTime();

                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return newestEntryUpdateTimestamp;
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
        List<VertretungsEntry> list = new ArrayList<>();
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery(
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
                                "GROUP BY entry.entry_id;");
                try {
                    while (resultSet.next()) {
                        VertretungsEntry entry = new VertretungsEntry(
                                resultSet.getInt("entry_id"),
                                resultSet.getTimestamp("registration_timestamp").toLocalDateTime(),
                                resultSet.getTimestamp("representation_date").toLocalDateTime().toLocalDate(),
                                resultSet.getString("class"),
                                resultSet.getString("default_hour"),
                                resultSet.getString("default_room"),
                                resultSet.getString("default_teacher_short")+" ("+resultSet.getString("default_teacher_long")+")",
                                resultSet.getString("default_subject"));
                        entry.setLastEntryUpdate(getLastVertretungsEntryUpdate(entry));
                        list.add(entry);
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return list;
    }

    public static VertretungsEntryUpdate getLastVertretungsEntryUpdate(VertretungsEntry entry){
        if(entry.getEntryId() == 0) return null;
        VertretungsEntryUpdate vertretungsEntryUpdate = null;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM entry_update " +
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
                try {
                    if(resultSet.next()) {
                        //TODO: locally save vars or verify they are saved locally - Working for testing...
                        VertretungsEntryUpdate entryUpdate = new VertretungsEntryUpdate(entry, resultSet.getTimestamp("register_datetime.timestamp_time").toLocalDateTime());
                        if(resultSet.getString("entry_note.note_name") != null && !resultSet.getString("entry_note.note_name").equalsIgnoreCase("null")) entryUpdate.setNote(resultSet.getString("entry_note.note_name"));
                        if(resultSet.getString("entry_hour.hour_name") != null && !resultSet.getString("entry_hour.hour_name").equalsIgnoreCase("null")) entryUpdate.setHour(resultSet.getString("entry_hour.hour_name"));
                        if(resultSet.getString("entry_room.room_name") != null && !resultSet.getString("entry_room.room_name").equalsIgnoreCase("null")) entryUpdate.setRoom(resultSet.getString("entry_room.room_name"));
                        if(resultSet.getString("entry_teacher.teacher_name") != null && !resultSet.getString("entry_teacher.teacher_name").equalsIgnoreCase("null")) entryUpdate.setTeacherLong(resultSet.getString("entry_teacher.teacher_name"));
                        if(resultSet.getString("entry_teacher.teacher_short") != null && !resultSet.getString("entry_teacher.teacher_short").equalsIgnoreCase("null")) entryUpdate.setTeacherShort(resultSet.getString("entry_teacher.teacher_short"));
                        if(resultSet.getString("entry_subject.subject_name") != null && !resultSet.getString("entry_subject.subject_name").equalsIgnoreCase("null")) entryUpdate.setSubject(resultSet.getString("entry_subject.subject_name"));
                        vertretungsEntryUpdate = entryUpdate;
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return vertretungsEntryUpdate;
    }

    public static List<String> getAllData(long chatId) {
        List<String> list = new ArrayList<>();
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM user WHERE chat_id=" + chatId);
                try {
                    if (resultSet.next()) {
                        list.add("Vorname: " + resultSet.getString("user_first_name"));
                        list.add("Nachname: " + resultSet.getString("user_last_name"));
                        list.add("Beitritt: "+resultSet.getTimestamp("registration_timestamp").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        list.add(" ");
                        list.add("Alle notifications & reminds:");
                        for (String s : getAllNotifyingClassesByChatId(chatId)) {
                            List<String> reminds = SQLManager.getAllReminderByClassAndChatId(s, chatId);
                            if(reminds.isEmpty()){
                                list.add("- " + s);
                            }else{
                                list.add("- "+s);
                                for (String remind : reminds) {
                                    list.add("  - "+remind);
                                }
                            }
                        }
                    }

                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return list;
    }

    public static void deleteNotification(long chatId, String className) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("DELETE FROM notification WHERE user_id=(SELECT user_id FROM user WHERE chat_id=" + chatId + ") AND class='" + className + "';");
    }

    public static boolean exists(String table, String column, String value) {
        boolean exists = false;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table + " WHERE " + column + "='" + value + "'");
                try {
                    if (resultSet.next()) exists = true;
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return exists;
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

    public static boolean insertNewNotification(Long chatId, String className) {
       return VertretungsPlanBot.getSqlConnector().executeUpdateWithFeedBack("INSERT INTO notification(user_id, class) " +
                "VALUES " +
                "((SELECT user_id FROM user WHERE chat_id=" + chatId + ") , '" + className + "');");
    }

    public static boolean existsNotification(Long chatId, String className){
        boolean exists = false;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM notification " +
                        "LEFT JOIN user ON notification.user_id = user.user_id " +
                        "WHERE notification.class = '"+className+"' AND chat_id = "+chatId);
                try {
                    if (resultSet.next()) exists = true;
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return exists;
    }

    public static List<Long> getAllChatIDsByNotifyClass(String className) {
        List<Long> list = new ArrayList<>();
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT chat_id FROM notification" +
                        " INNER JOIN user ON notification.user_id = user.user_id" +
                        " WHERE class='" + className + "'");
                try {
                    while (resultSet.next()) {
                        list.add(resultSet.getLong("chat_id"));
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return list;
    }

    public static List<String> getAllNotifiesClassesByUserId(int userId) {
        List<String> list = new ArrayList<>();

        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT class FROM notification" +
                        " WHERE user_id=" + userId);
                try {
                    while (resultSet.next()) {
                        list.add(resultSet.getString("class"));
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return list;
    }

    public static List<String> getAllNotifyingClassesByChatId(long chatId) {
        List<String> list = new ArrayList<>();
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT class FROM notification" +
                        " INNER JOIN user ON notification.user_id = user.user_id" +
                        " WHERE chat_id =" + chatId);
                try {
                    while (resultSet.next()) {
                        list.add(resultSet.getString("class"));
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return list;
    }

    public static boolean isRegistered(long chatId) {
        boolean exists = false;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT user_id FROM user WHERE chat_id=" + chatId);
                try {
                    if (resultSet.next()) exists = true;
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return exists;

    }

    public static List<VertretungsEntry> getAllRelevantEntriesByClass(String className) {
        List<VertretungsEntry> list = new ArrayList<>();
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery(
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
                try {
                    while (resultSet.next()) {
                        VertretungsEntry entry = new VertretungsEntry(
                                resultSet.getInt("entry_id"),
                                resultSet.getTimestamp("registration_timestamp").toLocalDateTime(),
                                resultSet.getTimestamp("representation_date").toLocalDateTime().toLocalDate(),
                                resultSet.getString("class"),
                                resultSet.getString("default_hour"),
                                resultSet.getString("default_room"),
                                resultSet.getString("default_teacher_short")+" ("+resultSet.getString("default_teacher_long")+")",
                                resultSet.getString("default_subject"));
                        entry.setLastEntryUpdate(getLastVertretungsEntryUpdate(entry));
                        list.add(entry);
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return list;
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
        List<String> list = new ArrayList<>();
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT hour FROM remind " +
                        "INNER JOIN notification ON notification.notification_id = remind.notification_id " +
                        "INNER JOIN user ON user.chat_id = "+ chatId+" " +
                        "WHERE notification.class = '"+className+"' AND user.user_id = notification.user_id;");
                try {
                    while (resultSet.next()) {
                        list.add(resultSet.getString("hour"));
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
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
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT chat_id, hour, class FROM remind " +
                        "LEFT JOIN notification ON notification.notification_id = remind.notification_id " +
                        "LEFT JOIN user ON user.user_id = notification.user_id " +
                        "WHERE hour='"+currentTime+"' OR hour='T"+currentTime+"'");
                try {
                    while (resultSet.next()) {
                        list.add(new Remind(resultSet.getLong("chat_id"), resultSet.getString("class"), resultSet.getString("hour")));
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return list;
    }

    public static List<Long> getAllChatIds() {
        List<Long> list = new ArrayList<>();
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT chat_id FROM user;");
                try {
                    while (resultSet.next()) {
                        list.add(resultSet.getLong("chat_id"));
                    }
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return list;
    }

    public static void deleteEntry(int entryId) {
        VertretungsPlanBot.getSqlConnector().executeUpdateWithFeedBack("DELETE FROM entry WHERE entry_id="+entryId);
    }

    public static boolean hasAcceptedCleverBot(Long chatId){
        boolean agreed = false;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery("SELECT clever_bot_agree.user_id FROM clever_bot_agree " +
                        "LEFT JOIN user ON user.user_id = clever_bot_agree.user_id " +
                        "WHERE user.chat_id = "+chatId+";");
                try {
                    if (resultSet.next()) agreed = true;
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return agreed;
    }
    public static void agreeCleverBot(Long chatId){
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO clever_bot_agree(user_id) VALUES ((SELECT user_id FROM user WHERE chat_id="+chatId+"));");
    }
    public static void insertNewConversation(long chatId, String input, String output){
        //VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO clever_bot_log(user_id, message, answer) VALUES ((SELECT user_id FROM user WHERE chat_id="+chatId+"), '"+input+"', '"+output+"');");

        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO clever_bot_log(user_id, message, answer) VALUES ((SELECT user_id FROM user WHERE chat_id="+chatId+"), ?, ?);");
            try {
                statement.setString(1, input);
                statement.setString(2, output);
                statement.executeUpdate();
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
    }

}
