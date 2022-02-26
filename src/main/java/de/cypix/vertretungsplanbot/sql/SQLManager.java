package de.cypix.vertretungsplanbot.sql;

import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    public static void insertNewEntry(VertretungsEntry entry) {
        //TODO: Fix String bilder with "," and "'"
        StringBuilder query = new StringBuilder("INSERT INTO entry(");
        query.append("last_refresh_timestamp,");
        query.append("representation_date,");
        query.append("class,");
        query.append("default_hour,");
        query.append("default_room,");
        query.append("default_teacher,");
        query.append("default_subject");
        if (entry.getNote() != null) query.append(",").append("note");
        if (entry.getNewHour() != null) query.append(",").append("new_hour");
        if (entry.getNewRoom() != null) query.append(",").append("new_room");
        if (entry.getNewTeacher() != null) query.append(",").append("new_teacher");
        if (entry.getNewSubject() != null) query.append(",").append("new_subject");
        query.append(") VALUES (");
        query.append("'").append(entry.getLastRefreshTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("'");
        query.append(", '").append(entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("'");
        query.append(", '").append(entry.getClassName()).append("'");
        query.append(", '").append(entry.getDefaultHour()).append("'");
        query.append(", '").append(entry.getDefaultRoom()).append("'");
        query.append(", '").append(entry.getDefaultTeacher()).append("'");
        query.append(", '").append(entry.getDefaultSubject()).append("'");
        if (entry.getNote() != null) query.append(", '").append(entry.getNote()).append("'");
        if (entry.getNewHour() != null) query.append(", '").append(entry.getNewHour()).append("'");
        if (entry.getNewRoom() != null) query.append(", '").append(entry.getNewRoom()).append("'");
        if (entry.getNewTeacher() != null) query.append(", '").append(entry.getNewTeacher()).append("'");
        if (entry.getNewSubject() != null) query.append(", '").append(entry.getNewSubject()).append("'");
        query.append(")");

        VertretungsPlanBot.getSqlConnector().executeUpdate(query.toString());
    }

    public static void updateEntry(VertretungsEntry entry) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("UPDATE entry SET note='" + entry.getNote() +
                "', new_hour='" + entry.getNewHour() +
                "', new_room='" + entry.getNewRoom() +
                "', new_teacher='" + entry.getNewTeacher() +
                "', new_subject='" + entry.getNewSubject() + "'" +
                "WHERE " +
                "representation_date='" + entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' " +
                "AND " +
                "class='" + entry.getClassName() + "' " +
                "AND " +
                "default_hour='" + entry.getDefaultHour() + "';");
    }

    public static List<VertretungsEntry> getAllRelevantEntries() {
        //TODO: Check if it's properly working
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry WHERE representation_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY)");
        return getVertretungsEntries(rs);
    }

    public static void deleteNotification(long chatId, String className){
        VertretungsPlanBot.getSqlConnector().executeUpdate("DELETE FROM notification WHERE user_id=(SELECT user_id FROM user WHERE chat_id="+chatId+") AND class='"+className+"';");
    }

    @NotNull
    private static List<VertretungsEntry> getVertretungsEntries(ResultSet rs) {
        List<VertretungsEntry> list = new ArrayList<>();
        try {
            while (rs.next()) {
                list.add(new VertretungsEntry(
                        rs.getInt("entry_id"),
                        rs.getTimestamp("registration_timestamp").toLocalDateTime(),
                        rs.getTimestamp("last_refresh_timestamp").toLocalDateTime(),
                        rs.getTimestamp("representation_date").toLocalDateTime().toLocalDate(),
                        rs.getString("class"),
                        rs.getString("default_hour"),
                        rs.getString("default_room"),
                        rs.getString("default_teacher"),
                        rs.getString("default_subject"),
                        rs.getString("note"),
                        rs.getString("new_hour"),
                        rs.getString("new_room"),
                        rs.getString("new_teacher"),
                        rs.getString("new_subject")));
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
                        rs.getTimestamp("last_refresh_timestamp").toLocalDateTime(),
                        rs.getTimestamp("representation_date").toLocalDateTime().toLocalDate(),
                        rs.getString("class"),
                        rs.getString("default_hour"),
                        rs.getString("default_room"),
                        rs.getString("default_teacher"),
                        rs.getString("default_subject"),
                        rs.getString("note"),
                        rs.getString("new_hour"),
                        rs.getString("new_room"),
                        rs.getString("new_teacher"),
                        rs.getString("new_subject")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime getLastRegisteredRefresh() {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT last_refresh_timestamp FROM entry ORDER BY last_refresh_timestamp DESC LIMIT 1");
        try {
            if (rs != null) {
                if (rs.next()) {
                    return rs.getTimestamp("last_refresh_timestamp").toLocalDateTime();
                }
            }
        } catch (SQLException e) {
            System.out.println("Returning now-1day because no entries...");
            return LocalDateTime.now().minusDays(1);
        }
        return LocalDateTime.now().minusDays(1);
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

    public static boolean isRegistered(long chatId){
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT user_id FROM user WHERE chat_id="+chatId);
        try {
            if(rs.next())return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<VertretungsEntry> getAllRelevantEntriesByClass(String className) {
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry WHERE class='" + className + "' AND representation_date >= CURRENT_DATE;");
        return getVertretungsEntries(rs);
    }
}
