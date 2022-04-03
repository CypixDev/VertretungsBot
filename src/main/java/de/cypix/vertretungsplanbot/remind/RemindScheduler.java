package de.cypix.vertretungsplanbot.remind;

import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import de.cypix.vertretungsplanbot.vertretungsplan.VertretungsEntry;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RemindScheduler extends Thread{

    private static final Logger logger = Logger.getLogger(RemindScheduler.class);


    private boolean stop = false;

    @Override
    public void run() {
        try {
            while (keepRunning()) {
                //Check if some sessions has been expired
                VertretungsPlanBot.getCleverBotManager().checkOutdated();

                String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

                for (Remind remind : SQLManager.getAllRemindsByTime(currentTime)) {
                    for (VertretungsEntry entry : SQLManager.getAllRelevantEntriesByClass(remind.getClassName())) {
                        if(remind.isNextDay()){
                            if(entry.getRepresentationDate().isEqual(LocalDate.now().plusDays(1))){
                                VertretungsPlanBot.getBot().execute(new SendMessage(remind.getChat_id(), entry.getSendUpdateMessage("Erinnerung")));
                            }
                        }else{
                            if(entry.getRepresentationDate().isEqual(LocalDate.now())){
                                VertretungsPlanBot.getBot().execute(new SendMessage(remind.getChat_id(), entry.getSendUpdateMessage("Erinnerung")));
                            }
                        }
                    }
                }


                Thread.sleep(60000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void Stop() {
        this.stop = true;
    }

    private synchronized boolean keepRunning() {
        return !this.stop;
    }
}
