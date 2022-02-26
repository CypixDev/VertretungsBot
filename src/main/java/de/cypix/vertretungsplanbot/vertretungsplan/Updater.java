package de.cypix.vertretungsplanbot.vertretungsplan;

import com.pengrad.telegrambot.request.SendMessage;
import de.cypix.vertretungsplanbot.main.VertretungsPlanBot;
import de.cypix.vertretungsplanbot.sql.SQLManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class Updater extends Thread {

    private static LocalDateTime lastRefreshTimeStamp = null;
    private static LocalDate representationDate = null;

    @Override
    public void run() {
        try {
            while (keepRunning()) {
                try {
                    URL url = new URL("https://btr-rs.de/btr-old/service-vertretungsplan.php");
                    Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));

                    if(!getLastRefresh(scanner).isEqual(Objects.requireNonNull(SQLManager.getLastRegisteredRefresh()))){
                        //Updating database....
                        List<VertretungsEntry> newEntries = filter(scanner);
                        List<VertretungsEntry> oldEntries = SQLManager.getAllRelevantEntries();

                        //TODO: check if something is deleted
                        if(newEntries.size() == oldEntries.size()){System.out.println("Anzahl einträge gleich geblieben");
                        }else if(newEntries.size() > oldEntries.size()){ System.out.println("Es gibt neue einträge!");
                        }else {System.out.println("Es sind weniger einträge...");}

                        for (VertretungsEntry newEntry : newEntries) {
                            //If none fits it is probably a new entry
                            boolean exist = false;
                            for (VertretungsEntry oldEntry : oldEntries) {
                                switch(newEntry.compareTo(oldEntry)){
                                    case -1: //different
                                        //Nothing to do...
                                        break;
                                    case 0://same
                                        exist = true;
                                        //Nothing to do....
                                        break;

                                    case 1://same defaults
                                        exist = true;
                                        SQLManager.updateEntry(newEntry);
                                        for (Long allChatIDsByNotifyClass : SQLManager.getAllChatIDsByNotifyClass(newEntry.getClassName())) {
                                            VertretungsPlanBot.getBot().execute(new SendMessage(allChatIDsByNotifyClass, "Änderung in "+newEntry.getNewSubject()+" und so weiter...."));
                                        }

                                         */
                                    break;
                            }
                        }
                        if (!exist) {
                            SQLManager.insertNewEntry(newEntry);
                            for (Long allChatIDsByNotifyClass : SQLManager.getAllChatIDsByNotifyClass(newEntry.getClassName())) {
                                VertretungsPlanBot.getBot().execute(new SendMessage(allChatIDsByNotifyClass,
                                        "Neuer eintrag Für den " + newEntry.getRepresentationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "\n" +
                                                "Klasse: " + newEntry.getClassName() + "\n" +
                                                "Stunde: " + newEntry.getDefaultHour() + "\n" +
                                                "Fach: " + newEntry.getDefaultSubject() + "\n" +
                                                "Anmerkung: " + newEntry.getNote()));
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                long chatId = 259699517; //Chat id von Pius


                Thread.sleep(6000); //second
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

    public static List<VertretungsEntry> filter(Scanner scanner) {
        System.out.println("Starting filtering....");
        String pattStart = ("<tr style='background-color: #FFFFFF;'>");
        List<VertretungsEntry> list = new ArrayList<>();

        String line = "";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains(pattStart)) {
                line = line.replace("<tr style='background-color: #FFFFFF;'><td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Klasse: " + line);
                String defaultClass = line;
                line = scanner.nextLine(); //skip "Lehrer"
                line = line.replace("&nbsp;", " ");

                //Stunden
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");

                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Stunden: " + line);
                String defaultHour = line;

                //Raum
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Raum: " + line);
                String defaultRoom = line;

                //Lehrer
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("&ouml;", "ö");
                line = line.replace("&auml;", "ä");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Lehrer: " + line);
                String defaultTeacher = line;
                //Fach
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Fach: " + line);
                String defaultSubject = line;
                //Grund
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Grund: " + line);
                //Fach
                line = scanner.nextLine();
                line = line.replace("&nbsp;", " ");
                line = line.replace("&auml;", "ä");
                line = line.replace("<td rowspan='2' style='padding: 2px; border: 1px solid #000000; vertical-align: top;'>", "");
                line = line.replace("</td></tr>", "");
                System.out.println("Aktion: " + line);
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
                    System.out.println("Stunden: " + line);
                    String newHour = line;
                    //Raum
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Raum: " + line);
                    String newRoom = line;
                    //Lehrer
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("&ouml;", "ö");
                    line = line.replace("&auml;", "ä");
                    line = line.replace("<td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Lehrer: " + line);
                    String newTeacher = line;
                    //Fach
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Fach: " + line);
                    String newSubject = line;
                    //Grund
                    line = scanner.nextLine();
                    line = line.replace("&nbsp;", " ");
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td></tr>", "");
                    System.out.println("Grund: " + line);

                    //Adding entry full
                    VertretungsEntry entry = new VertretungsEntry(LocalDateTime.now(),
                            lastRefreshTimeStamp,
                            representationDate,
                            defaultClass,
                            defaultHour,
                            defaultRoom,
                            defaultTeacher,
                            defaultSubject,
                            note,
                            newHour,
                            newRoom,
                            newTeacher,
                            newSubject);
                    list.add(entry);

                }
                /* ADDING ALWAYS EVERYTHING!
                System.out.println("---------------");
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
                System.out.println("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

                line = line.split(": ")[1];

                System.out.println("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));
                lastRefreshTimeStamp = LocalDateTime.parse(line, lastRefreshDateFormatter);
                //System.out.println(line);
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
        System.out.println("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

        line = line.split(": ")[1];

        System.out.println("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));
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
                  System.out.println("Klasse: "+line);
                  line = scanner.nextLine(); //skip "Lehrer"

                  //Stunden
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  System.out.println("Stunden: "+line);
                  //Raum
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  System.out.println("Raum: "+line);
                  //Lehrer
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  System.out.println("Lehrer: "+line);
                  //Fach
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  System.out.println("Fach: "+line);
                  //Grund
                  line = scanner.nextLine();
                  line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                  line = line.replace("</td>", "");
                  System.out.println("Grund: "+line);
                  //Fach
                  line = scanner.nextLine();
                  line = line.replace("<td rowspan='2' style='padding: 2px; border: 1px solid #000000; vertical-align: top;'>", "");
                  line = line.replace("</td></tr>", "");
                  System.out.println("Aktion: "+line);
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
                      System.out.println("Stunden: "+line);
                      //Raum
                      line = scanner.nextLine();
                      line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td>", "");
                      System.out.println("Raum: "+line);
                      //Lehrer
                      line = scanner.nextLine();
                      line = line.replace("<td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td>", "");
                      System.out.println("Lehrer: "+line);
                      //Fach
                      line = scanner.nextLine();
                      line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td>", "");
                      System.out.println("Fach: "+line);
                      //Grund
                      line = scanner.nextLine();
                      line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                      line = line.replace("</td></tr>", "");
                      System.out.println("Grund: "+line);
                  }
                  System.out.println();
                  //Line ende
              }else if(line.contains("Planabweichungen")){
                  line = line.replace("<h3 style='font-size: 18px; color: #6666FF;'>", "");
                  line = line.replace("<br><font size='-1'>", "");
                  line = line.replace("</h3>", "");

                  System.out.println(line);
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
                System.out.println("Klasse: " + line);
                line = scanner.nextLine(); //skip "Lehrer"

                //Stunden
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Stunden: " + line);
                //Raum
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Raum: " + line);
                //Lehrer
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Lehrer: " + line);
                //Fach
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Fach: " + line);
                //Grund
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Grund: " + line);
                //Fach
                line = scanner.nextLine();
                line = line.replace("<td rowspan='2' style='padding: 2px; border: 1px solid #000000; vertical-align: top;'>", "");
                line = line.replace("</td></tr>", "");
                System.out.println("Aktion: " + line);
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
                    System.out.println("Stunden: " + line);
                    //Raum
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Raum: " + line);
                    //Lehrer
                    line = scanner.nextLine();
                    line = line.replace("<td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Lehrer: " + line);
                    //Fach
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Fach: " + line);
                    //Grund
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td></tr>", "");
                    System.out.println("Grund: " + line);
                }
                System.out.println();
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
                System.out.println("Für den " + LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN) + " gilt:");

                line = line.split(": ")[1];

                System.out.println("Zuletzt aktuallisiert am um: " + LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));

                System.out.println(line);
            }
            //line.indexOf()
        }
    }
}
