package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.PrintWriter;

public class Log{
    private static String allLogs="Nombre de secondes;Coût;T°Moyenne;T°Extérieure\n";
    private String fileName;
    private String directoryName ="logs";
    private String logFilePath; //= directoryName+"/"+fileName; 

    private LocalDateTime currentDateTime;
    private File logFile; 
    private File directoryfile;

    public void addLog(int time, double cost, double exteriorTemperature, double averageTemperature){
        allLogs += time+";"+String.format("%.1f", cost)+";"+String.format("%.1f", averageTemperature)+";"+exteriorTemperature+"\n";
    }

    public void createLogFile(){
        System.out.println("create log file");
        currentDateTime = LocalDateTime.now();
        DateTimeFormatter currentDateTimeInGoodFormat = DateTimeFormatter.ofPattern("ddMMyy_HHmmss");//permet d'afficher la date et l'heure 
        //comme on le veut, attention il faut laisser en majuscule et minuscule comme c'est mtn sinon peut produire autre resultat
        fileName = currentDateTime.format(currentDateTimeInGoodFormat)+".log";
        logFilePath = directoryName+"/"+fileName; 
        try {
            directoryfile = new File(directoryName);
            if(!directoryfile.exists()){ //si le dossier log n'existe pas on le créé
            System.out.println("creation dossier");
                directoryfile.mkdirs();
            }
            logFile = new File(logFilePath);
            if (logFile.createNewFile()){
                System.out.println("if logfile.createNe");
                PrintWriter writer=new PrintWriter(logFile);//writer permet d ecrire dans le fichier
                writer.write(allLogs);
                writer.close();
            }
        } catch (Exception e) {
            System.out.println("error when creating file");
        }
    }
}