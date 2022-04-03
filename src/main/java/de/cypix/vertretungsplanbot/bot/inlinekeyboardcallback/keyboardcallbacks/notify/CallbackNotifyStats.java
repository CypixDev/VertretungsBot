package de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.keyboardcallbacks.notify;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import de.cypix.vertretungsplanbot.bot.inlinekeyboardcallback.KeyboardCallback;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class CallbackNotifyStats implements KeyboardCallback {

    private static final Logger logger = Logger.getLogger(CallbackNotifyStats.class);

    @Override
    public boolean handleCallBack(String key, Update update, Chat chat, HashMap<String, String> data) {
        if(!getKey().equalsIgnoreCase(key)) return false;
        String className = data.get("class");

        StringBuilder str = new StringBuilder();
        str.append("Für Daten ab dem 08.03.2022\n\n");
        str.append("➡️ Einträge: ");
        str.append(getCountHours(className));
        str.append("\n");
        str.append("\uD83C\uDFC6 Top Lehrer: ");
        str.append(getTopTeacher(className));

        EditMessageText editMessageText = new EditMessageText(chat.id(), update.callbackQuery().message().messageId(),
                "Statistiken für "+data.get("class")+"\n"+str.toString())
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(new InlineKeyboardMarkup());

        VertretungsPlanBot.getBot().execute(editMessageText);


        return true;
    }

    @Override
    public String getKey() {
        return "stats";
    }

    private int getCountHours(String className){
        int count = -1;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery(
                        "SELECT COUNT(entry_id) as ccc FROM entry WHERE class_id = (SELECT class_id FROM entry_class WHERE class_name = '"+className+"');");
                try {
                    if(resultSet.next())
                        count = resultSet.getInt("ccc");
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return count;
    }

    private String getTopTeacher(String className){
        String teacher = null;
        Connection connection = VertretungsPlanBot.getSqlConnector().getConnection();
        try {
            Statement statement = connection.createStatement();
            try {
                ResultSet resultSet = statement.executeQuery(
                        "SELECT entry_teacher.teacher_name as ccc, COUNT(entry_id) as cccc, default_teacher_id FROM entry " +
                                "LEFT JOIN entry_teacher ON entry.default_teacher_id = entry_teacher.teacher_id " +
                                "WHERE class_id = (SELECT class_id FROM entry_class WHERE class_name = '"+className+"') " +
                                "GROUP BY default_teacher_id " +
                                "ORDER BY COUNT(entry_id) DESC LIMIT 1");
                try {
                    if(resultSet.next())
                        teacher = resultSet.getString("ccc")+"("+resultSet.getString("cccc")+")";
                } finally {
                    resultSet.close();
                }
            } finally {
                statement.close();
            }
        }catch (SQLException ex) {
            logger.error(ex);
        }
        return teacher;
    }
}
