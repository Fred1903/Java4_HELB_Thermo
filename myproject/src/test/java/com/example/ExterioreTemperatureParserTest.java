package com.example;

import java.beans.Transient;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExterioreTemperatureParserTest {
    ExteriorTemperatureParser exteriorTemperatureParser = new ExteriorTemperatureParser("uselessString"); //ctor demande le nom du fichier en parametre
    //mais ici on l'utilise pas donc un met juste un string en parametre pour que ca fonctionne 
    Scanner scanner;
    String valueInString;
    int value;
    String [] correctValuesArray = {"15","40","0","26"};
    String [] falseValuesArray = {"2,6","besiktas","41","-2"};



    @Test
    public void checkValueShouldAddNewExteriorTemperatureToExteriorTemperatureList(){ //de cette maniere avec le foreach fait que 1 test et pas 4
        //valueInString = "25"; //car le scanner attend un string pas un int
        //int value = 25;
        int cpt=0;
        for (String correctValue : correctValuesArray) {
            valueInString = correctValue;
            value = Integer.valueOf(correctValue);
            scanner = new Scanner(valueInString);
            exteriorTemperatureParser.checkValue(scanner);
            assertEquals(value, exteriorTemperatureParser.getExteriorTemperaturesList().get(cpt++).getExteriorTemperature()); //0 car on a ajt que 1 element     
        } 
        
    }

    @Test
    public void checkValueShouldNotAddNewExteriorTemperatureToExteriorTemperatureList(){
        valueInString = "7,5";
        scanner = new Scanner(valueInString);
        exteriorTemperatureParser.checkValue(scanner);
        assertTrue(exteriorTemperatureParser.getExteriorTemperaturesList().isEmpty()); //si vide veut dire que ne l'a pas ajouté
    }
    
 

}
