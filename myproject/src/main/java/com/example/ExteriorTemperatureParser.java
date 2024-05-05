package com.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ExteriorTemperatureParser {
    private ArrayList<ExteriorTemperature> exteriorTemperaturesList = new ArrayList<ExteriorTemperature>();
    private int currentIndex = 0;
    private int maxIndex = 0;
    private final int one = 1;
    private final static int MINIMUM_EXTERIOR_TEMPERATURE = 0;
    private final static int MAXIMUM_EXTERIOR_TEMPERATURE = 40;
    
    public ExteriorTemperatureParser(String filename){
        parse(filename);
        maxIndex = exteriorTemperaturesList.size();
    }

    public ExteriorTemperature getNexExteriorTemperature(){
        //si le currentIndex est avant l'avant-dernière valeur alors on renvoie currentindex ++, sinon juste currentIndex car sinon indexOutOfBonds 
        //System.out.println("has next temp:"+hasNextTemperature());
        //System.out.println("currIndex+1<maxIndex :"+((currentIndex+one)<maxIndex));
        if(hasNextTemperature() && (currentIndex+one)<maxIndex){
            ExteriorTemperature temp=exteriorTemperaturesList.get(currentIndex);
            System.out.println("temp :"+temp.getExteriorTemperature());
            System.out.println("if index:"+currentIndex);
            currentIndex++;
            
            //currentIndex++;
            return temp;}  //affiche 0 2 4 6 et index++ affiche 0 1 3 5 ..
        return exteriorTemperaturesList.get(currentIndex);
    }

    public ArrayList<ExteriorTemperature> getExteriorTemperaturesList(){
        return exteriorTemperaturesList;
    }

    //true si on a pas déjà eu toutes les temperatures exterieures
    public boolean hasNextTemperature(){
        return (currentIndex < maxIndex);
    }

    private void parse(String filename){
        try(Scanner scanner = new Scanner(new File(filename))){
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                try(Scanner scannerOfLine = new Scanner(line)){ //dans ce scanner on va avoir la valeur de chaque ligne en particulier
                    checkValue(scannerOfLine);
                    /*if(scannerOfLine.hasNextInt()){ //si la prochaine valeur est un int alors on rentre dans le if 
                        int temperature = scannerOfLine.nextInt();
                        if(temperature>=MINIMUM_EXTERIOR_TEMPERATURE && temperature <= MAXIMUM_EXTERIOR_TEMPERATURE){
                            ExteriorTemperature exteriorTemperature = new ExteriorTemperature(temperature);
                            exteriorTemperaturesList.add(exteriorTemperature); //si ok alors on ajoute a notre liste de températures
                        }
                    }*/
                }
                catch(NoSuchElementException exception){ //NoSuchElementException est l'erreur que j'avais dans mon terminal avec IoException ca crash
                    System.err.println("Error reading the line : "+line);
                } 
            }
        }
        catch(IOException exception){
            System.err.println("Error reading the file : "+exception.getMessage());
        }
    }

    public void checkValue(Scanner scannerOfLine){
        if(scannerOfLine.hasNextInt()){ //si la prochaine valeur est un int alors on rentre dans le if 
            int temperature = scannerOfLine.nextInt();
            if(temperature>=MINIMUM_EXTERIOR_TEMPERATURE && temperature <= MAXIMUM_EXTERIOR_TEMPERATURE){
                ExteriorTemperature exteriorTemperature = new ExteriorTemperature(temperature);
                exteriorTemperaturesList.add(exteriorTemperature); //si ok alors on ajoute a notre liste de températures
            }
        }
    }
}
