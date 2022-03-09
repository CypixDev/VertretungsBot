package de.cypix.vertretungsplanbot.sql;

import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import org.apache.log4j.Logger;

import java.sql.*;

public class SQLConnector {

    private static final Logger logger = Logger.getLogger(SQLConnector.class);


    private static SQLConnector instance;

    private String host, database, user, password;
    private int port;
    private Connection connection;
    private boolean firstConnected;


    public SQLConnector(String host, String database, String user, String password, int port) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
        firstConnected = false;


        SQLConnector.instance = this;
        connect();
        createTable();
    }

    public SQLConnector(boolean fromConfig){
        if(fromConfig){
            this.host = VertretungsPlanBot.getConfigManager().getSQLHost();
            this.database = VertretungsPlanBot.getConfigManager().getSQLDatabase();
            this.user = VertretungsPlanBot.getConfigManager().getSQLUser();
            this.password = VertretungsPlanBot.getConfigManager().getSQLPassword();
            this.port = VertretungsPlanBot.getConfigManager().getSQLPort();
            firstConnected = false;

            SQLConnector.instance = this;
            connect();
            createTable();
        }
    }

    private void createTable() {
        if(isConnected()){
            //Still usable
            executeUpdate("CREATE TABLE IF NOT EXISTS user(" +
                    "user_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "chat_id LONG, " +
                    "user_name VARCHAR(50), " +
                    "user_first_name VARCHAR(50), " +
                    "user_last_name VARCHAR(50));");

            executeUpdate("CREATE TABLE IF NOT EXISTS notification(" +
                    "notification_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT NOT NULL, " +
                    "class CHAR(6) NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE," +
                    "CONSTRAINT uc_class UNIQUE (user_id, class));");

            executeUpdate("CREATE TABLE IF NOT EXISTS remind(" +
                    "remind_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "notification_id INT NOT NULL, " +
                    "hour TINYINT NOT NULL, " +
                    "FOREIGN KEY (notification_id) REFERENCES notification(notification_id) ON DELETE CASCADE)");

            //Entry - default stuff
            executeUpdate("CREATE TABLE IF NOT EXISTS entry_timestamp(" +
                    "timestamp_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "timestamp_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_class(" +
                    "class_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "class_name CHAR(6) NOT NULL)");
            executeUpdate("CREATE TABLE IF NOT EXISTS entry_room(" +
                    "room_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "room_name CHAR(4) NOT NULL)");
            executeUpdate("CREATE TABLE IF NOT EXISTS entry_hour(" +
                    "hour_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "hour_name CHAR(6) NOT NULL)");
            executeUpdate("CREATE TABLE IF NOT EXISTS entry_teacher(" +
                    "teacher_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "teacher_short CHAR(2) NOT NULL," +
                    "teacher_name VARCHAR(50) NOT NULL)");
            executeUpdate("CREATE TABLE IF NOT EXISTS entry_subject(" +
                    "subject_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "subject_name CHAR(4) NOT NULL)");
            executeUpdate("CREATE TABLE IF NOT EXISTS entry_note(" +
                    "note_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "note_name VARCHAR(50) NOT NULL)");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry("+
                    "entry_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "registration_timestamp_id INT," +
                    "representation_date_id INT," +
                    "class_id INT NOT NULL, " +
                    "default_hour_id INT NOT NULL, " +
                    "default_room_id INT NOT NULL, " +
                    "default_teacher_id INT NOT NULL, " +
                    "default_subject_id INT NOT NULL," +
                    "FOREIGN KEY (registration_timestamp_id) REFERENCES entry_timestamp(timestamp_id), "+
                    "FOREIGN KEY (representation_date_id) REFERENCES entry_timestamp(timestamp_id), "+
                    "FOREIGN KEY (class_id) REFERENCES entry_class(class_id)," +
                    "FOREIGN KEY (default_hour_id) REFERENCES entry_hour(hour_id)," +
                    "FOREIGN KEY (default_room_id) REFERENCES entry_room(room_id)," +
                    "FOREIGN KEY (default_teacher_id) REFERENCES entry_teacher(teacher_id)," +
                    "FOREIGN KEY (default_subject_id) REFERENCES entry_subject(subject_id))");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update(" +
                    "entry_update_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "entry_id INT, " +
                    "registration_timestamp_id INT, " +
                    "FOREIGN KEY (entry_id) REFERENCES entry(entry_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (registration_timestamp_id) REFERENCES entry_timestamp(timestamp_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_note(" +
                    "entry_update_note_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "note_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (note_id) REFERENCES entry_note(note_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_hour(" +
                    "entry_update_hour_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "hour_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (hour_id) REFERENCES entry_hour(hour_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_room(" +
                    "entry_update_room_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "room_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (room_id) REFERENCES entry_room(room_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_teacher(" +
                    "entry_update_teacher_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "teacher_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (teacher_id) REFERENCES entry_teacher(teacher_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_subject(" +
                    "entry_update_subject_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "subject_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (subject_id) REFERENCES entry_subject(subject_id));");
        }
    }

    @Deprecated
    public ResultSet getResultSet(String query) {
        if (isConnected()) {
            try {
                //PreparedStatement preparedStatement = connection.prepareStatement(query);
                //ResultSet rs = preparedStatement.getResultSet();
                return connection.createStatement().executeQuery(query);
                //preparedStatement.close();
                //return rs;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            reconnect();
            try {
                //PreparedStatement preparedStatement = connection.prepareStatement(query);
                //ResultSet rs = preparedStatement.getResultSet();
                return connection.createStatement().executeQuery(query);
                //preparedStatement.close();
                //return rs;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isConnected() {
        try {
            if (connection == null || !connection.isValid(10) || connection.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
    Return true if success
    Returns false if not successfully
     */

    public boolean executeUpdateWithFeedBack(String query){
        if(isConnected()){
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                logger.error(e);
            }
        }else{
            reconnect();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                logger.error(e);
            }
        }
        return false;
    }

    public ResultSet getResultSetSafely(String query){
        ResultSet set = null;
        if (isConnected()) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                set = preparedStatement.getResultSet();
                preparedStatement.close();
            } catch (SQLException e) {
                logger.error(e);
            }
        }else{
            reconnect();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                set = preparedStatement.getResultSet();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error(e);
            }
        }
        return set;
    }

    @Deprecated
    public void executeUpdate(String qry) {
        if (isConnected()) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qry);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else {
            reconnect();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(qry);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
                            + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin",
                    "" + user, password);
            logger.info("Successfully connected to Database!");
            firstConnected = true;
        } catch (SQLException e) {
            logger.error(e);
            firstConnected = false;
        }
    }

    public void closeConnection() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
            } finally {
                connection = null;
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static SQLConnector getInstance() {
        return instance;
    }

    private void reconnect() {
        if (firstConnected) {
            if (!isConnected()) {
                connect();
            }
        }
    }
}