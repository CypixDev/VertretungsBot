package de.cypix.vertretungsplanbot.vertretungsplan;

import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class Updater extends Thread {


    //DONT TOUCH!

    private static LocalDateTime lastRefreshTimeStamp = null;
    private static LocalDate representationDate = null;

    public static boolean simulateLoss = false;
    public static boolean simulateUpdate = false;
    public static boolean simulateAdd = false;

    private static final Logger logger = Logger.getLogger(Updater.class);


    @Override
    public void run() {
        try {
            while (keepRunning()) {
                if((LocalDateTime.now().getHour() < 23 && LocalDateTime.now().getHour() >= 1) || VertretungsPlanBot.getConfigManager().isMaintenance()){
                    try {
                        URL url = new URL("https://btr-rs.de/btr-old/service-vertretungsplan.php");
                        Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));

                    /*TODO just update if there are new entries...
                       if(!getLastRefresh(scanner).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                            .equals(SQLManager.getLastRegisteredRefresh().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))){
                        //Updating database....

                     */
                        List<VertretungsEntry> newEntries = filter(scanner);
                        List<VertretungsEntry> oldEntries = SQLManager.getAllRelevantEntries();
                        List<VertretungsEntry> oldEntriesLeft = new ArrayList<>(oldEntries);

                        /*Konzept wegfallende einträge prüfen
                            Die alten Einträge durchgehen und alle löschen die auch im neune vorkommen
                          ODER
                            Die Neue Liste durchgehen und schauen welche der Alten einträge nicht mehr drinnen sind
                         */

                        if (newEntries.size() == oldEntries.size()) {
                            logger.debug("Anzahl einträge gleich geblieben");
                        } else if (newEntries.size() > oldEntries.size()) {
                            logger.debug("Es gibt neue einträge!");
                        } else {
                            logger.debug("Es sind weniger einträge...");
                        }


                        if(simulateAdd){
                            newEntries.add(new VertretungsEntry(LocalDateTime.now(), LocalDate.now(), "TEST", "XXXX", "YYYY", "DR (Frau Dr. Hold)", "KAKA"));
                            simulateAdd = false;
                        }
                        for (VertretungsEntry newEntry : newEntries) {
                            //If none fits it is probably a new entry
                            if(simulateUpdate){
                                newEntry.getLastEntryUpdate().setTeacherLong("LEEELLLL");
                                newEntry.getLastEntryUpdate().setTeacherShort("DE");
                                simulateUpdate = false;
                            }
                            boolean exist = false;
                            for (VertretungsEntry oldEntry : oldEntries) {
                                switch (newEntry.compareTo(oldEntry)) {
                                        /*
                                            0 means same
                                            1 absolut different
                                            2 defaults same
                                         */
                                    case 0: //same
                                        if(!simulateLoss){
                                            oldEntriesLeft.remove(oldEntry);
                                        }else simulateLoss = false;
                                        exist = true;

                                        //Nothing to do....
                                        break;
                                    case 1: //different
                                        //could be other one or not existing
                                        //Nothing to do....
                                        break;
                                    case 2://Aktuallisieren -- Defaults same but not defaults not

                                        exist = true;
                                        oldEntriesLeft.remove(oldEntry);
                                        SQLManager.insertNewUpdate(oldEntry.getEntryId(), newEntry.getLastEntryUpdate());

                                        logger.info("Entry updated["+newEntry.toString()+"\n Update: "+newEntry.getLastEntryUpdate().toString()+"]\n");

                                        //Just build one time for all notifications
                                        StringBuilder builder = new StringBuilder();
                                        builder.append("Aktuallisierung für den ")
                                                .append(newEntry.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                                                .append("\n");

                                        builder.append("Klasse: ").append(newEntry.getClassName()).append("\n");
                                        builder.append("Stunde: ").append(newEntry.getDefaultHour()).append("\n");
                                        builder.append("Fach: ").append(newEntry.getDefaultSubject()).append("\n");
                                        if(newEntry.getLastEntryUpdate().getNote() != null && !newEntry.getLastEntryUpdate().getNote().equals("null"))
                                            builder.append("Anmerkung: ").append(newEntry.getLastEntryUpdate().getNote()).append("\n");
                                        if(newEntry.getLastEntryUpdate().getTeacherLong() != null && !newEntry.getLastEntryUpdate().getTeacherLong().equals("null"))
                                            builder.append("Vertreter: ").append(newEntry.getLastEntryUpdate().getTeacherLong()).append("\n");
                                        if(newEntry.getLastEntryUpdate().getSubject() != null && !newEntry.getLastEntryUpdate().getSubject().equals("null"))
                                            builder.append("Neues Fach: ").append(newEntry.getLastEntryUpdate().getSubject()).append("\n");
                                        if(newEntry.getLastEntryUpdate().getRoom() != null && !newEntry.getLastEntryUpdate().getRoom().equals("null"))
                                            builder.append("Neuer Raum: ").append(newEntry.getLastEntryUpdate().getRoom()).append("\n");
                                        if(newEntry.getLastEntryUpdate().getHour() != null && !newEntry.getLastEntryUpdate().getHour().equals("null"))
                                            builder.append("Neue Stunde: ").append(newEntry.getLastEntryUpdate().getHour()).append("\n");


                                        for (Long chatId : SQLManager.getAllChatIDsByNotifyClass(newEntry.getClassName())) {
                                            VertretungsPlanBot.getBot().execute(new SendMessage(chatId, builder.toString()));
                                        }

                                        break;
                                }
                            }
                            if (!exist) {

                                if(!newEntry.getClassName().equals("TEST")){ //For simulation....
                                    SQLManager.insertNewEntry(newEntry);
                                }else{
                                    newEntry.setLastEntryUpdate(new VertretungsEntryUpdate(newEntry, LocalDateTime.now()));
                                }
                                logger.info("Added new entry["+newEntry.toString()+"\n Update: "+newEntry.getLastEntryUpdate().toString()+"]\n");
                                for (Long allChatIDsByNotifyClass : SQLManager.getAllChatIDsByNotifyClass(newEntry.getClassName())) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("Neuer Eintrag für den ")
                                            .append(newEntry.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                                            .append("\n");

                                    builder.append("Klasse: ").append(newEntry.getClassName()).append("\n");
                                    builder.append("Stunde: ").append(newEntry.getDefaultHour()).append("\n");
                                    builder.append("Fach: ").append(newEntry.getDefaultSubject()).append("\n");
                                    if(newEntry.getLastEntryUpdate().getNote() != null && !newEntry.getLastEntryUpdate().getNote().equals("null"))
                                        builder.append("Anmerkung: ").append(newEntry.getLastEntryUpdate().getNote()).append("\n");
                                    if(newEntry.getLastEntryUpdate().getTeacherLong() != null && !newEntry.getLastEntryUpdate().getTeacherLong().equals("null"))
                                        builder.append("Vertreter: ").append(newEntry.getLastEntryUpdate().getTeacherLong()).append("\n");
                                    if(newEntry.getLastEntryUpdate().getSubject() != null && !newEntry.getLastEntryUpdate().getSubject().equals("null"))
                                        builder.append("Neues Fach: ").append(newEntry.getLastEntryUpdate().getSubject()).append("\n");
                                    if(newEntry.getLastEntryUpdate().getRoom() != null && !newEntry.getLastEntryUpdate().getRoom().equals("null"))
                                        builder.append("Neuer Raum: ").append(newEntry.getLastEntryUpdate().getRoom()).append("\n");
                                    if(newEntry.getLastEntryUpdate().getHour() != null && !newEntry.getLastEntryUpdate().getHour().equals("null"))
                                        builder.append("Neue Stunde: ").append(newEntry.getLastEntryUpdate().getHour()).append("\n");

                                    if(!VertretungsPlanBot.getConfigManager().isMaintenance() || allChatIDsByNotifyClass == 259699517)
                                        VertretungsPlanBot.getBot().execute(new SendMessage(allChatIDsByNotifyClass,builder.toString()));
                                }
                            }
                        }
                        if(!oldEntriesLeft.isEmpty()){
                            logger.info("Folgende Entries sind weggefallen:");
                            for (VertretungsEntry entry : oldEntriesLeft) {
                                logger.info("- "+entry.getEntryId());
                                VertretungsPlanBot.getBot().execute(new SendMessage(259699517,"Eintrag weggefallen:\n" +
                                        "Datum: "+entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"\n" +
                                        "Klasse: "+entry.getClassName()+"\n" +
                                        "Stunden: "+entry.getDefaultHour()));
                                SQLManager.deleteEntry(entry.getEntryId());
                                for (Long allChatIDsByNotifyClass : SQLManager.getAllChatIDsByNotifyClass(entry.getClassName())) {
                                    VertretungsPlanBot.getBot().execute(new SendMessage(allChatIDsByNotifyClass,"Eintrag weggefallen:\n" +
                                            "Datum: "+entry.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"\n" +
                                            "Klasse: "+entry.getClassName()+"\n" +
                                            "Stunden: "+entry.getDefaultHour()));
                                }
                            }
                        }

                    } catch (Exception e) {
                        logger.error(e);
                        VertretungsPlanBot.getBot().execute(new SendMessage(259699517, "Error in Updater.... pls CHECK ME!!!!\n"+e.getMessage()));
                        simulateAdd = false;
                    }

                    long chatId = 259699517; //Chat id von Pius


                    Thread.sleep(60000); //second

                    //Vergleiche: 23.02.2022 11:03:40 und 23.02.2022 13:11:52
                }
            }
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    private boolean stop = false;

    public synchronized void Stop() {
        this.stop = true;
    }

    private synchronized boolean keepRunning() {
        return !this.stop;
    }

    private boolean containsEntry(List<VertretungsEntry> list, VertretungsEntry entryCheck){
        for (VertretungsEntry entry : list) {
            if(entry.compareTo(entryCheck) == 1) return true;
        }
        return false;
    }

    public static List<VertretungsEntry> filter(Scanner scanner) {
        logger.debug("Starting filtering....");
        String pattStart = ("<tr style='background-color: #FFFFFF;'>");
        List<VertretungsEntry> list = new ArrayList<>();

        String line = "";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains(pattStart)) {
                line = line.replace("<tr style='background-color: #FFFFFF;'><td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.debug("Klasse: " + line);
                String defaultClass = line;
                line = scanner.nextLine(); //skip "Lehrer"
                line = line.replace("&nbsp;", " ");

                //Stunden
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");

                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.debug("Stunden: " + line);
                String defaultHour = line;

                //Raum
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.debug("Raum: " + line);
                String defaultRoom = line;

                //Lehrer
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("&ouml;", "ö");
                line = line.replace("&auml;", "ä");
                line = line.replace("&uuml;", "ü");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.debug("Lehrer: " + line);
                String defaultTeacher = line;
                //Fach
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.debug("Fach: " + line);
                String defaultSubject = line;
                //Grund
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.debug("Grund: " + line);
                //Fach
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("&ouml;", "ö");
                line = line.replace("&auml;", "ä");
                line = line.replace("&Auml;;", "Ä");
                line = line.replace("&uuml;", "ü");
                line = line.replace("<td rowspan='2' style='padding: 2px; border: 1px solid #000000; vertical-align: top;'>", "");
                line = line.replace("</td></tr>", "");
                logger.debug("Anmerkung: " + line);

                String note = line;
                line = scanner.nextLine();
                line = scanner.nextLine();
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("&ouml;", "ö");
                line = line.replace("&auml;", "ä");
                line = line.replace("&uuml;", "ü");
                if (line.contains(">.246485645.<")) {
                    //Fällt aus, kein weiteres lesen nötig
                } else {
                    //Stunden
                    //line = scanner.nextLine(); did it above...
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    logger.debug("Stunden: " + line);
                    String newHour = line;
                    //Raum
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    logger.debug("Raum: " + line);
                    String newRoom = line;
                    //Lehrer
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("&ouml;", "ö");
                    line = line.replace("&auml;", "ä");
                    line = line.replace("&uuml;", "ü");
                    line = line.replace("<td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    logger.debug("Lehrer: " + line);
                    String newTeacher = line;
                    //Fach
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    logger.debug("Fach: " + line);
                    String newSubject = line;
                    //Grund
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td></tr>", "");
                    logger.debug("Grund: " + line);

                    //Adding entry full

                    VertretungsEntry entry = new VertretungsEntry(LocalDateTime.now(),
                            representationDate,
                            defaultClass,
                            defaultHour,
                            defaultRoom,
                            defaultTeacher,
                            defaultSubject);
                    if(!entry.getDefaultHour().equals("0..0")){
                        VertretungsEntryUpdate entryUpdate = new VertretungsEntryUpdate(entry, LocalDateTime.now());
                        entryUpdate.setNote(note);
                        entryUpdate.setHour(newHour);
                        entryUpdate.setRoom(newRoom);
                        //Check is needed her because of split.... Others don't need because 'null' will be filtered out...
                        if(!newTeacher.equalsIgnoreCase("null") && !newTeacher.equalsIgnoreCase("") && !newTeacher.equalsIgnoreCase(" ")){
                            if(newTeacher.contains("Dr.")){
                                entryUpdate.setTeacherLong(newTeacher.split(" ")[1].replace("(", "")+" "+newTeacher.split(" ")[2].replace(")", "")+" "+newTeacher.split(" ")[3].replace(")", ""));
                            }else{
                                entryUpdate.setTeacherLong(newTeacher.split(" ")[1].replace("(", "")+" "+newTeacher.split(" ")[2].replace(")", ""));
                            }
                            entryUpdate.setTeacherShort(newTeacher.split(" ")[0]);
                        }
                        entryUpdate.setSubject(newSubject);

                        entry.setLastEntryUpdate(entryUpdate);

                        list.add(entry);
                    } else {
                        /*
                        Ist nötig da das eindeutige identifizieren an einigen stellen fehl schlagen kann....
                        Bspw.: Es kann sein das der gleiche lehrer in der 1..2 und 3..4 dran kommt und dann
                            ist die erste leiste exakt gleich....
                         */
                        logger.debug("Additional hour is ignored for "+entry.getClassName());
                    }

                }
                /* ADDING ALWAYS EVERYTHING!
                logger.info("---------------");
                //substring 25/26
                //Adding entry (fällt aus)
                VertretungsEntry entry = new VertretungsEntry(LocalDateTime.now(),
                        lastRefreshTimeStamp,
                        representationDate,
                        defaultClass,
                        defaultHour,
                        defaultRoom,
                        defaultTeacher,
                        defaultSubject,
                        note);
                list.add(entry);*/

                //Line ende
            } else if (line.contains("Planabweichungen")) {
                line = line.replace("<h3 style='font-size: 18px; color: #6666FF;'>", "");
                line = line.replace("<br><font size='-1'>", "");
                line = line.replace("</h3>", "");

                DateTimeFormatter lastRefreshDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                DateTimeFormatter representationDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


                line = line.split(", ")[1];
                String tmp = line.split(" - ")[0];

                //LocalDate.parse(tmp, representationDateFormatter);
                representationDate = LocalDate.parse(tmp, representationDateFormatter);
                logger.debug("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

                line = line.split(": ")[1];

                logger.debug("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));
                lastRefreshTimeStamp = LocalDateTime.parse(line, lastRefreshDateFormatter);
                //logger.info(line);
                //NOT NEEDED ???currentDate = line;
            }
            //line.indexOf()
        }
        return list;
    }

    private LocalDateTime getLastRefresh(Scanner scanner) {

        String line = "";

        while (!line.contains("Planabweichungen")) {
            line = scanner.nextLine();
        }
        line = line.replace("<h3 style='font-size: 18px; color: #6666FF;'>", "");
        line = line.replace("<br><font size='-1'>", "");
        line = line.replace("</h3>", "");

        DateTimeFormatter lastRefreshDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        DateTimeFormatter representationDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


        line = line.split(", ")[1];
        String tmp = line.split(" - ")[0];

        //LocalDate.parse(tmp, representationDateFormatter);
        representationDate = LocalDate.parse(tmp, representationDateFormatter);
        logger.debug("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

        line = line.split(": ")[1];

        logger.info("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));
        lastRefreshTimeStamp = LocalDateTime.parse(line, lastRefreshDateFormatter);

        return LocalDateTime.parse(line, lastRefreshDateFormatter);
    }

    public static void filter() {
        String pattStart = ("<tr style='background-color: #FFFFFF;'>");
        Scanner scanner = new Scanner(System.in);
        try {
            URL url = new URL("https://btr-rs.de/btr-old/service-vertretungsplan.php");
            scanner = new Scanner(new InputStreamReader(url.openStream()));
            Updater.filter(scanner);
        } catch (IOException e) {
            logger.error(e);
        }

        String line = "";
        String currentDate = "";
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains(pattStart)) {
                line = line.replace("<tr style='background-color: #FFFFFF;'><td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.info("Klasse: " + line);
                line = scanner.nextLine(); //skip "Lehrer"

                //Stunden
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.info("Stunden: " + line);
                //Raum
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.info("Raum: " + line);
                //Lehrer
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.info("Lehrer: " + line);
                //Fach
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.info("Fach: " + line);
                //Grund
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                logger.info("Grund: " + line);
                //Fach
                line = scanner.nextLine();
                line = line.replace("<td rowspan='2' style='padding: 2px; border: 1px solid #000000; vertical-align: top;'>", "");
                line = line.replace("</td></tr>", "");
                logger.info("Aktion: " + line);
                line = scanner.nextLine();
                line = scanner.nextLine();
                line = scanner.nextLine();
                if (line.contains(">..<")) {
                    //Fällt aus, kein weiteres lesen nötig
                } else {
                    //Stunden
                    //line = scanner.nextLine(); did it above...
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    logger.info("Stunden: " + line);
                    //Raum
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    logger.info("Raum: " + line);
                    //Lehrer
                    line = scanner.nextLine();
                    line = line.replace("<td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    logger.info("Lehrer: " + line);
                    //Fach
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    logger.info("Fach: " + line);
                    //Grund
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td></tr>", "");
                    logger.info("Grund: " + line);
                }
                logger.info("");
                //Line ende
            } else if (line.contains("Planabweichungen")) {

                line = line.replace("<h3 style='font-size: 18px; color: #6666FF;'>", "");
                line = line.replace("<br><font size='-1'>", "");
                line = line.replace("</h3>", "");

                DateTimeFormatter lastRefreshDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
                DateTimeFormatter representationDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


                line = line.split(", ")[1];
                String tmp = line.split(" - ")[0];

                LocalDate.parse(tmp, representationDateFormatter);
                logger.debug("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

                line = line.split(": ")[1];

                logger.debug("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));

                logger.debug(line);
            }
            //line.indexOf()
        }
    }
}
