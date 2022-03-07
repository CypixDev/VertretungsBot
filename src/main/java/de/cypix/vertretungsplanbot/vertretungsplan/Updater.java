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

    private static final boolean DEBUG = false;

    private static final Logger logger = Logger.getLogger(Updater.class);


    @Override
    public void run() {
        try {
            while (keepRunning()) {
                if(/*LocalDateTime.now().getHour() < 23 && LocalDateTime.now().getHour() >= 1*/true){
                    try {
                        URL url = new URL("https://btr-rs.de/btr-old/service-vertretungsplan.php");
                        Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));

                        if(DEBUG) logger.info("Vergleiche: "
                                + getLastRefresh(scanner).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) +
                                " und "
                                + SQLManager.getLastRegisteredRefresh().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
                    /*if(!getLastRefresh(scanner).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
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

                        //TODO: check if something is deleted
                        if (newEntries.size() == oldEntries.size()) {
                            logger.info("Anzahl einträge gleich geblieben");
                        } else if (newEntries.size() > oldEntries.size()) {
                            logger.info("Es gibt neue einträge!");
                        } else {
                            logger.info("Es sind weniger einträge...");
                            //TODO: Check lost entries
                        }


                        if(simulateAdd){
                            newEntries.add(new VertretungsEntry(LocalDateTime.now(), LocalDate.now(), "TEST", "XXXX", "YYYY", "HHAA", "KAKA"));
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
                                    /*TODO: check....
                                    if(!getLastRefresh(scanner).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                                            .equals(SQLManager.getLastRegisteredRefresh().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))){
                                     */
/*                                    logger.info("COMPARE: Defaults");
                                    logger.info(oldEntry.toString());
                                    logger.info(newEntry.toString());
                                    logger.info();*/

                                        exist = true;
                                        oldEntriesLeft.remove(oldEntry);
                                        SQLManager.insertNewUpdate(oldEntry.getEntryId(), newEntry.getLastEntryUpdate());
                                        //SQLManager.updateEntry(newEntry);
                                        //TODO: Check what has changed and change it
                                        if(DEBUG) logger.info("entry updated!");
                                        /*for (Long allChatIDsByNotifyClass : SQLManager.getAllChatIDsByNotifyClass(newEntry.getClassName())) {
                                            VertretungsPlanBot.getBot().execute(new SendMessage(allChatIDsByNotifyClass, "Änderung in "+newEntry.getNewSubject()+" und so weiter...."));
                                        }*/
                                        VertretungsPlanBot.getBot().execute(new SendMessage(259699517,"Aktuallisierung für:\n" +
                                                "Datum: "+newEntry.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"\n" +
                                                "Klasse: "+newEntry.getClassName()+"\n" +
                                                "Stunden: "+newEntry.getDefaultHour()));

                                        break;
                                }
                            }
                            if (!exist) {

                                if(!newEntry.getClassName().equals("TEST")){ //For simulation....
                                    SQLManager.insertNewEntry(newEntry);
                                }
                                logger.info("Added new entry!");
                                for (Long allChatIDsByNotifyClass : SQLManager.getAllChatIDsByNotifyClass(newEntry.getClassName())) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("Neuer eintrag für den ")
                                            .append(newEntry.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                                            .append("\n");

                                    builder.append("Klasse: ").append(newEntry.getClassName()).append("\n");
                                    builder.append("Stunde: ").append(newEntry.getDefaultHour()).append("\n");
                                    builder.append("Fach: ").append(newEntry.getDefaultSubject()).append("\n");/*
                                    if(newEntry.getNote() != null && !newEntry.getNote().equals("null"))
                                        builder.append("Anmerkung: ").append(newEntry.getNote()).append("\n");
                                    if(newEntry.getNewTeacher() != null && !newEntry.getNewTeacher().equals("null"))
                                        builder.append("Vertreter: ").append(newEntry.getNewTeacher()).append("\n");
                                    if(newEntry.getNewSubject() != null && !newEntry.getNewSubject().equals("null"))
                                        builder.append("Neues Fach: ").append(newEntry.getNewSubject()).append("\n");
                                    if(newEntry.getNewRoom() != null && !newEntry.getNewRoom().equals("null"))
                                        builder.append("Neuer Raum: ").append(newEntry.getNewRoom()).append("\n");
                                    if(newEntry.getNewHour() != null && !newEntry.getNewHour().equals("null"))
                                        builder.append("Neue Stunde: ").append(newEntry.getNewHour()).append("\n");*/

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
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    long chatId = 259699517; //Chat id von Pius


                    Thread.sleep(60000); //second

                    //Vergleiche: 23.02.2022 11:03:40 und 23.02.2022 13:11:52
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
        logger.info("Starting filtering....");
        String pattStart = ("<tr style='background-color: #FFFFFF;'>");
        List<VertretungsEntry> list = new ArrayList<>();

        String line = "";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains(pattStart)) {
                line = line.replace("<tr style='background-color: #FFFFFF;'><td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                if(DEBUG) logger.info("Klasse: " + line);
                String defaultClass = line;
                line = scanner.nextLine(); //skip "Lehrer"
                line = line.replace("&nbsp;", " ");

                //Stunden
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");

                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                if(DEBUG) logger.info("Stunden: " + line);
                String defaultHour = line;

                //Raum
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                if(DEBUG) logger.info("Raum: " + line);
                String defaultRoom = line;

                //Lehrer
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("&ouml;", "ö");
                line = line.replace("&auml;", "ä");
                line = line.replace("&uuml;", "ü");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                if(DEBUG) logger.info("Lehrer: " + line);
                String defaultTeacher = line;
                //Fach
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                if(DEBUG) logger.info("Fach: " + line);
                String defaultSubject = line;
                //Grund
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                if(DEBUG) logger.info("Grund: " + line);
                //Fach
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("&auml;", "ä");
                line = line.replace("<td rowspan='2' style='padding: 2px; border: 1px solid #000000; vertical-align: top;'>", "");
                line = line.replace("</td></tr>", "");
                if(DEBUG) logger.info("Aktion: " + line);
                String note = line;
                line = scanner.nextLine();
                line = scanner.nextLine();
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                if (line.contains(">.246485645.<")) {
                    //Fällt aus, kein weiteres lesen nötig
                } else {
                    //Stunden
                    //line = scanner.nextLine(); did it above...
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    if(DEBUG) logger.info("Stunden: " + line);
                    String newHour = line;
                    //Raum
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    if(DEBUG) logger.info("Raum: " + line);
                    String newRoom = line;
                    //Lehrer
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("&ouml;", "ö");
                    line = line.replace("&auml;", "ä");
                    line = line.replace("&uuml;", "ü");
                    line = line.replace("<td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    if(DEBUG) logger.info("Lehrer: " + line);
                    String newTeacher = line;
                    //Fach
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    if(DEBUG) logger.info("Fach: " + line);
                    String newSubject = line;
                    //Grund
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td></tr>", "");
                    if(DEBUG) logger.info("Grund: " + line);

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
                            entryUpdate.setTeacherLong(newTeacher.split(" ")[1].replace("(", "")+" "+newTeacher.split(" ")[2].replace(")", ""));
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
                        logger.info("Additional hour is ignored for "+entry.getClassName());
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
                if(DEBUG) logger.info("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

                line = line.split(": ")[1];

                logger.info("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));
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
        if(DEBUG) logger.info("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

        line = line.split(": ")[1];

        logger.info("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));
        lastRefreshTimeStamp = LocalDateTime.parse(line, lastRefreshDateFormatter);

        return LocalDateTime.parse(line, lastRefreshDateFormatter);
    }

    /*  public static String filter(Scanner scanner){ //WORKING BACKUP

          String pattStart = ("<tr style='background-color: #FFFFFF;'>");

          String line = "";
          String currentDate = "";
          while(scanner.hasNextLine()) {

              line=scanner.nextLine();
              if (line.contains(pattStart)) {
                  line = line.replace("<tr style='background-color: #FFFFFF;'><td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  logger.info("Klasse: "+line);
                  line = scanner.nextLine(); //skip "Lehrer"

                  //Stunden
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  logger.info("Stunden: "+line);
                  //Raum
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  logger.info("Raum: "+line);
                  //Lehrer
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  logger.info("Lehrer: "+line);
                  //Fach
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  logger.info("Fach: "+line);
                  //Grund
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  logger.info("Grund: "+line);
                  //Fach
                  line = scanner.nextLine();
                  line = line.replace("<td rowspan='2' style='padding: 2px; border: 1px solid #000000; vertical-align: top;'>", "");
                  line = line.replace("</td></tr>", "");
                  logger.info("Aktion: "+line);
                  line = scanner.nextLine();
                  line = scanner.nextLine();
                  line = scanner.nextLine();
                  if(line.contains(">..<")){
                      //Fällt aus, kein weiteres lesen nötig
                  }else{
                      //Stunden
                      //line = scanner.nextLine(); did it above...
                      line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td>", "");
                      logger.info("Stunden: "+line);
                      //Raum
                      line = scanner.nextLine();
                      line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td>", "");
                      logger.info("Raum: "+line);
                      //Lehrer
                      line = scanner.nextLine();
                      line = line.replace("<td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td>", "");
                      logger.info("Lehrer: "+line);
                      //Fach
                      line = scanner.nextLine();
                      line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td>", "");
                      logger.info("Fach: "+line);
                      //Grund
                      line = scanner.nextLine();
                      line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td></tr>", "");
                      logger.info("Grund: "+line);
                  }
                  logger.info();
                  //Line ende
              }else if(line.contains("Planabweichungen")){
                  line = line.replace("<h3 style='font-size: 18px; color: #6666FF;'>", "");
                  line = line.replace("<br><font size='-1'>", "");
                  line = line.replace("</h3>", "");

                  logger.info(line);
              }
              //line.indexOf()
          }
          return "";
      }*/
    public static void filter() {
        String pattStart = ("<tr style='background-color: #FFFFFF;'>");
        Scanner scanner = new Scanner(System.in);
        try {
            URL url = new URL("https://btr-rs.de/btr-old/service-vertretungsplan.php");
            scanner = new Scanner(new InputStreamReader(url.openStream()));
            Updater.filter(scanner);
        } catch (IOException e) {
            e.printStackTrace();
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
                if(DEBUG) logger.info("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

                line = line.split(": ")[1];

                logger.info("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));

                logger.info(line);
            }
            //line.indexOf()
        }
    }
}
