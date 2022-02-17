package de.cypix.vertretungsplanbot.vertretungsplan;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Updater extends Thread {

    @Override
    public void run() {
        try {
            try {
                URL url = new URL("https://btr-rs.de/btr-old/service-vertretungsplan.php");
                Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));
                Updater.filter(scanner);
            } catch (IOException e) {
                e.printStackTrace();
            }

            long chatId = 259699517; //Chat id von Pius


            sleep(60000); //second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<VertretungsEntry> filter(Scanner scanner) {

        String pattStart = ("<tr style='background-color: #FFFFFF;'>");

        String line = "";
        String currentDate = "";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.contains(pattStart)) {
                line = line.replace("<tr style='background-color: #FFFFFF;'><td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Klasse: " + line);
                String defaultClass = line;
                line = scanner.nextLine(); //skip "Lehrer"

                //Stunden
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Stunden: " + line);
                String defaultHour = line;
                //Raum
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Raum: " + line);
                String defaultRoom = line;
                //Lehrer
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Lehrer: " + line);
                String defaultTeacher = line;
                //Fach
                line = scanner.nextLine();
                line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                line = line.replace("</td>", "");
                System.out.println("Fach: " + line);
                String defaultSubject = line;
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
                String note = line;
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
                    String newHour = line;
                    //Raum
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Raum: " + line);
                    String newRoom = line;
                    //Lehrer
                    line = scanner.nextLine();
                    line = line.replace("<td style='font-weight: bold; padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Lehrer: " + line);
                    String newTeacher = line;
                    //Fach
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td>", "");
                    System.out.println("Fach: " + line);
                    String newSubject = line;
                    //Grund
                    line = scanner.nextLine();
                    line = line.replace("<td style='padding: 2px; border: 1px solid #000000;'>", "");
                    line = line.replace("</td></tr>", "");
                    System.out.println("Grund: " + line);
                }
                System.out.println("---------------");
                //substring 25/26


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
                System.out.println("Für den "+LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN)+" gilt:");

                line = line.split(": ")[1];

                System.out.println("Zuletzt aktuallisiert am um: "+LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));

                //System.out.println(line);
                currentDate = line;
            }
            //line.indexOf()
        }
        return null;
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
              System.out.println("Für den "+LocalDate.parse(tmp, representationDateFormatter).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN)+" gilt:");

              line = line.split(": ")[1];

              System.out.println("Zuletzt aktuallisiert am um: "+LocalDateTime.parse(line, lastRefreshDateFormatter).format(lastRefreshDateFormatter));

              System.out.println(line);
          }
          //line.indexOf()
      }
  }
}
