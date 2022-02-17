package de.cypix.vertretungsplanbot.sql;

import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLManager {

    public static void insertNewEntry(VertretungsEntry entry){
        VertretungsPlanBot.getSqlConnector().executeUpdate("INSERT INTO entry(" +
                "last_refresh_timestamp," +
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
                ") VALUES (" +
                entry.getLastRefreshTimeStamp()+"," +
                entry.getClassName()+"," +
                entry.getDefaultHour()+"," +
                entry.getDefaultRoom()+"," +
                entry.getDefaultTeacher()+"," +
                entry.getDefaultSubject()+"," +
                entry.getNote()+"," +
                entry.getNewHour()+"," +
                entry.getNewRoom()+"," +
                entry.getNewTeacher()+"," +
                entry.getNewSubject()+")");
    }

    public static List<VertretungsEntry> getAllEntries(){
        ResultSet rs = VertretungsPlanBot.getSqlConnector().getResultSet("SELECT * FROM entry LIMIT 20)");
        List<VertretungsEntry> list = new ArrayList<>();
        try{
            while(rs.next()){
                list.add(new VertretungsEntry(
                        rs.getInt("entry_id"),
                        rs.getTimestamp("registration_timestamp").toLocalDateTime(),
                        rs.getTimestamp("last_refresh_timestamp").toLocalDateTime(),
                        rs.getTimestamp("representation_date").toLocalDateTime(),
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
                        rs.getTimestamp("representation_date").toLocalDateTime(),
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

    /*public static long getPrivateChannelId(long discordId){
        if(isConnected()){
            ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * from private_channel WHERE (SELECT user_id from user where discord_id="+discordId+");");

            try {
                if(rs.next()){
                    return rs.getLong("private_channel_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    public static void insertUser(User user){
        if(getLocalIdByDiscordId(Long.parseLong(user.getId())) == -1){
            TasksCheckBot.getSqlConnector().executeUpdatee("INSERT INTO user(discord_id, discord_name) VALUES ("+user.getId()+", '"+user.getName()+"')");
        }
    }

    public static void insertPrivateChannelId(long userId, long privateChannelId){
        if(getPrivateChannelId(userId) == -1){
            TasksCheckBot.getSqlConnector().executeUpdatee("INSERT INTO private_channel() VALUES ((SELECT user_id FROM user WHERE discord_id="+userId+"), "+privateChannelId+")");
        }
    }

    public static boolean isConnected() {
        return TasksCheckBot.getSqlConnector() != null && TasksCheckBot.getSqlConnector().isConnected();
    }

    public static void insertNewTask(SchoolSubject schoolSubject, String till, String description){
        TasksCheckBot.getSqlConnector().executeUpdatee("INSERT INTO task(subject_id, task_description, task_deadline) VALUES ("+schoolSubject.getId()+", '"+description+"', '"+till+"');");
    }


    public static List<SchoolTask> getAllTasks(SchoolSubject schoolSubject){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * FROM task WHERE subject_id="+schoolSubject.getId()+"" +
                " AND task_deadline > DATE_SUB(NOW(),INTERVAL 2 DAY)");
        List<SchoolTask> list = new ArrayList<>();
        try{
            while(rs.next()){
                list.add(new SchoolTask(rs.getInt("task_id"),
                        schoolSubject,
                        rs.getString("task_description"),
                        rs.getString("task_link"),
                        rs.getTimestamp("task_deadline").toLocalDateTime(),
                        rs.getInt("userid")));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isIgnoringSubject(long userId, SchoolSubject schoolSubject){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * FROM user_ignore WHERE user_id="+userId+";");
        try{
            while(rs.next()){
                if(rs.getInt("subject_id") == schoolSubject.getId()) return true;
            }
        }catch(SQLException ignored){

        }
        return false;
    }
    public static boolean isIgnoringSubject(User user, SchoolSubject schoolSubject){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT subject_id FROM user_ignore " +
                "INNER JOIN user ON user.user_id=user_ignore.user_id WHERE discord_id="+user.getIdLong()+";");
        try{
            while(rs.next()){
                if(rs.getInt("subject_id") == schoolSubject.getId()) return true;
            }
        }catch(SQLException ignored){ }
        return false;
    }

    public static boolean deleteFromIgnoreSubject(User user, SchoolSubject schoolSubject){
        if(isIgnoringSubject(user, schoolSubject)){
            TasksCheckBot.getSqlConnector().executeUpdatee("DELETE FROM user_ignore WHERE user_id=" +
                    "(SELECT user_id from user WHERE discord_id="+user.getIdLong()+")" +
                    " AND subject_id="+schoolSubject.getId());
            return true;
        }
        return false;
    }

    public static boolean taskExists(int taskId){
        try {
            return TasksCheckBot.getSqlConnector().getResultSet("SELECT * from task WHERE task_id="+taskId).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void updateDescription(int taskId, String description){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdatee("UPDATE task SET task_description='"+description+"' WHERE task_id='"+taskId+";");
        }
    }

    public static int getUserId(long discordId){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user_id FROM user WHERE discord_id="+discordId+";");
        try {
            if(rs.next()){
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean isTaskFinished(int userId, int taskId){
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT * FROM finish_user WHERE user_id="+userId+" AND task_id="+taskId+";");
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void updateSubject(int taskId, int subjectId){
        if(taskExists(taskId)){
            TasksCheckBot.getSqlConnector().executeUpdatee("UPDATE task SET subject_id="+subjectId+" WHERE task_id="+taskId+";");
        }
    }

    public static void delTask(int taskId){
        TasksCheckBot.getSqlConnector().executeUpdatee("DELETE FROM task WHERE task_id="+taskId+";");
    }

    public static void delTasksFromSubject(SchoolSubject schoolSubject){
        TasksCheckBot.getSqlConnector().executeUpdatee("DELETE FROM task WHERE subject_id="+schoolSubject.getId()+";");
    }

    public static void archiveTask(int taskId){

    }

    public static void markAsNotFinish(int userId, int taskId) {
        try {
            TasksCheckBot.getSqlConnector().executeUpdate("DELETE FROM finish_user WHERE user_id="+userId+" AND task_id="+taskId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<ReminderTask> getAllReminderTasks() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user ON user.user_id=user_reminder.user_id");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }
    public static List<ReminderTask> getAllReminderTasksPerSecond() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user.user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user on user.user_id=user_reminder.user_id" +
                " WHERE time_unit=0");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }
    public static List<ReminderTask> getAllReminderTasksPerMinute() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user.user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user on user.user_id=user_reminder.user_id" +
                " WHERE time_unit=1");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }
    public static List<ReminderTask> getAllReminderTasksPerHour() throws SQLException{
        ResultSet rs = TasksCheckBot.getSqlConnector().getResultSet("SELECT user.user_id,time_before,discord_id,time_unit FROM user_reminder" +
                " JOIN user on user.user_id=user_reminder.user_id" +
                " WHERE time_unit=2");
        List<ReminderTask> list = new ArrayList<>();
        while(rs.next()){
            list.add(new ReminderTask(rs.getInt("user_id"), rs.getLong("discord_id"), rs.getInt("time_before"), TimeUnit.getById(rs.getInt("time_unit"))));
        }
        return list;
    }*/
}
