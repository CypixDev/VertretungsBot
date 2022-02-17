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
            executeUpdate("CREATE TABLE IF NOT EXISTS user(" +
                    "user_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "chat_id LONG, " +
                    "user_name VARCHAR(50), " +
                    "user_first_name VARCHAR(50), " +
                    "user_last_name VARCHAR(50));");

            executeUpdate("CREATE TABLE IF NOT EXISTS notification(" +
                    "user_id INT NOT NULL, class CHAR(6)," +
                    "FOREIGN KEY (user_id) PREFERENCE user(user_id) ON DELETE CASCADE," +
                    "CONSTRAINT uc_class UNIQUE (user_id, class));");

            executeUpdate("CREATE TABLE IF NOT EXISTS entry(" +
                    "entry_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "registration_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "last_refresh_timestamp TIMESTAMP NOT NULL," +
                    "class CHAR(6) NOT NULL, " +
                    "default_hour CHAR(6) NOT NULL, " +
                    "default_room CHAR(4) NOT NULL, " +
                    "default_teacher VARCHAR(50) NOT NULL, " +
                    "default_subject CHAR(4) NOT NULL, " +
                    "note VARCHAR(50) NOT NULL, " +
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
                    "new_subject));");




/*            executeUpdatee("CREATE TABLE IF NOT EXISTS user(user_id INT PRIMARY KEY AUTO_INCREMENT, discord_id LONG, discord_name VARCHAR(255));");
            executeUpdatee("CREATE TABLE IF NOT EXISTS private_channel(user_id INT, private_channel_id LONG);");
            executeUpdatee("CREATE TABLE IF NOT EXISTS finish_user(user_id INT, task_id INT);");
            executeUpdatee("CREATE TABLE IF NOT EXISTS user_ignore(user_id INT, subject_id TINYINT);");
            executeUpdatee("CREATE TABLE IF NOT EXISTS user_reminder(user_id INT, time_before INT, time_unit TINYINT)");*/
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