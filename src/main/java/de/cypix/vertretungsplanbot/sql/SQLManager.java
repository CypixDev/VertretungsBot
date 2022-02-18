package de.cypix.vertretungsplanbot.sql;

import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    public static void insertNewEntry(VertretungsEntry entry){
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry(" +
                "last_refresh_timestamp," +
                "representation_date," +
                "class," +
                "default_hour," +
                "default_room," +
                "default_teacher," +
                "default_subject," +
                "note," +
                "new_hour," +
                "new_room," +
                "new_teacher," +
                "new_subject" +
                ") VALUES ('" +
                entry.getLastRefreshTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"', '" +
                entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"', '" +
                entry.getClassName()+"', '" +
                entry.getDefaultHour()+"', '" +
                entry.getDefaultRoom()+"', '" +
                entry.getDefaultTeacher()+"', '" +
                entry.getDefaultSubject()+"', '" +
                entry.getNote()+"', '" +
                entry.getNewHour()+"', '" +
                entry.getNewRoom()+"', '" +
                entry.getNewTeacher()+"', '" +
                entry.getNewSubject()+"')");
    }

    public static void updateEntry(VertretungsEntry entry){
        VertretungsPlanBot.getSqlConnector().executeUpdate("UPDATE entry SET note='"+entry.getNote()+
                "', new_hour='"+entry.getNewHour()+
                "', new_room='"+entry.getNewRoom()+
                "', new_teacher='"+entry.getNewTeacher()+
                "', new_subject='"+entry.getNewSubject()+"'" +
                "WHERE " +
                "representation_date='"+entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"' " +
                "AND " +
                "class='"+ entry.getClassName()+"' " +
                "AND " +
                "default_hour='"+ entry.getDefaultHour()+"';");
    }

    public static List<VertretungsEntry> getAllRelevantEntries(){
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry WHERE representation_date >= CURRENT_TIMESTAMP");
        List<VertretungsEntry> list = new ArrayList<>();
        try{
            while(rs.next()){
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
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public static List<VertretungsEntry> getAllEntries(){
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry LIMIT 20");
        List<VertretungsEntry> list = new ArrayList<>();
        try{
            while(rs.next()){
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
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    public static VertretungsEntry getEntry(int entryId){
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry WHERE entry_id="+entryId);
        try{
            while(rs.next()){
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
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime getLastRegisteredRefresh(){
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT last_refresh_timestamp FROM entry ORDER BY last_refresh_timestamp DESC LIMIT 1");
        try{
            if(rs != null){
                if(rs.next()){
                    return rs.getTimestamp("last_refresh_timestamp").toLocalDateTime();
                }
            }
        }catch(SQLException e){
            System.out.println("Returning now-1day because no entries...");
            return LocalDateTime.now().minusDays(1);
        }
        return LocalDateTime.now().minusDays(1);
    }

    public static void insertNewUser(Long chatId, String firstName, String lastName) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO user(" +
                "chat_id," +
                "user_first_name," +
                "user_last_name"+
                ") VALUES (" +
                chatId +", '" +
                firstName+"', '" +
                lastName+"')");
    }
    public static void insertNewNotification(Long chatId, String className) {
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO notification(user_id, class) " +
                "VALUES " +
                "((SELECT user_id FROM user WHERE chat_id="+chatId+") , '"+className+"');");
    }

    public static List<Long> getAllChatIDsByNotifyClass(String className){
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT chat_id FROM notification" +
                " INNER JOIN user ON notification.user_id = user.user_id" +
                " WHERE class='"+className+"'");
        List<Long> list = new ArrayList<>();
        try{
            while(rs.next()){
                list.add(rs.getLong("chat_id"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }
    public static List<VertretungsEntry> getAllRelevantEntriesByClass(String className){
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry WHERE class='"+className+"' AND representation_date >= CURRENT_TIMESTAMP;");
        List<VertretungsEntry> list = new ArrayList<>();
        try{
            while(rs.next()){
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
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }
}
