package de.cypix.vertretungsplanbot.sql;

import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;

import java.sql.*;

public class SQLConnector {
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
                    "user_id INT NOT NULL, class CHAR(6)," +
                    "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE," +
                    "CONSTRAINT uc_class UNIQUE (user_id, class));");

            //OLD....
/*            executeUpdate("CREATE TABLE IF NOT EXISTS entry(" +
                    "entry_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "registration_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "last_refresh_timestamp TIMESTAMP NOT NULL," +
                    "representation_date DATE NOT NULL," +
                    "class CHAR(6) NOT NULL, " +
                    "default_hour CHAR(6) NOT NULL, " +
                    "default_room CHAR(4) NOT NULL, " +
                    "default_teacher VARCHAR(50) NOT NULL, " +
                    "default_subject CHAR(4) NOT NULL, " +
                    "note VARCHAR(50), " +
                    "new_hour CHAR(6), " +
                    "new_room CHAR(4), " +
                    "new_teacher VARCHAR(50), " +
                    "new_subject CHAR(4), " +
                    "CONSTRAINT uc_all_needed UNIQUE (" +
                    "last_refresh_timestamp,  " +
                    "class,  " +
                    "default_hour,  " +
                    "default_room, " +
                    "default_teacher, " +
                    "default_subject, " +
                    "note, " +
                    "new_hour,  " +
                    "new_room, " +
                    "new_teacher, " +
                    "new_subject));");*/

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
                    "FOREIGN KEY (entry_id) REFERENCES entry(entry_id), " +
                    "FOREIGN KEY (registration_timestamp_id) REFERENCES entry_timestamp(timestamp_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_note(" +
                    "entry_update_note_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "note_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id)," +
                    "FOREIGN KEY (note_id) REFERENCES entry_note(note_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_hour(" +
                    "entry_update_hour_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "hour_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id)," +
                    "FOREIGN KEY (hour_id) REFERENCES entry_hour(hour_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_room(" +
                    "entry_update_room_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "room_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id)," +
                    "FOREIGN KEY (room_id) REFERENCES entry_room(room_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_teacher(" +
                    "entry_update_teacher_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "teacher_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id)," +
                    "FOREIGN KEY (teacher_id) REFERENCES entry_teacher(teacher_id));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry_update_subject(" +
                    "entry_update_subject_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "entry_update_id INT," +
                    "subject_id INT," +
                    "FOREIGN KEY (entry_update_id) REFERENCES entry_update(entry_update_id)," +
                    "FOREIGN KEY (subject_id) REFERENCES entry_subject(subject_id));");
        }
    }


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
    public void executeUpdateee(String qry) throws SQLException{
        if (isConnected()) {
                PreparedStatement preparedStatement = connection.prepareStatement(qry);
                preparedStatement.executeUpdate();
                preparedStatement.close();
        }else {
            reconnect();
                PreparedStatement preparedStatement = connection.prepareStatement(qry);
                preparedStatement.executeUpdate();
                preparedStatement.close();
        }
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
                            + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin",
                    "" + user, password);
            System.out.println("Successfully connected to Database!");
            firstConnected = true;
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void reconnect(){
        if(firstConnected){
            if(!isConnected()){
                connect();
            }
        }
    }

    public boolean checkLogin(String user, String password) {
        ResultSet rs = getResultSet("SELECT * FROM users WHERE username='"+user+"' AND password='"+password+"';");
        try {
            if(rs.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return false;
    }
}