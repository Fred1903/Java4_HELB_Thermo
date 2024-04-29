package com.example;

public class Cell {
    private double temperature; 

    private boolean diffuseHeat=false;
    private boolean isDead=false;

    public void calculateTemperature(double [] adjacentItemsTemperatures){
        double totalTemperature = temperature;
        int totalNumberInfluences = 1; //la cellule elle meme
        for (double temperatureItem : adjacentItemsTemperatures) {
            //System.out.println("tempItem : "+temperatureItem);
            if(temperatureItem!=ThermoController.getDeadCellNoTemperature()){ //si la temperature = -500 cela signifie que c'est une cellule morte et on n'en tient pas en comtpe
                totalTemperature += temperatureItem;
                totalNumberInfluences++;
            }
        }
        //System.out.println("Total temp :"+totalTemperature);
        temperature = totalTemperature/totalNumberInfluences; //temperature = moyenne de tt les temperatures adjacentes + sa propre temp.
        //System.out.println("Temp :"+temperature);
    }


    public double getTemperature() {
        return temperature;
    }



    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }



    public boolean isHeatDiffuser() { //Attentions aux noms!!
        return diffuseHeat;
    }

    public boolean isCellDead(){ //cellule morte "getter"
        return isDead;
    }

    public void setDiffuseHeat(boolean diffuseHeat) {
        this.diffuseHeat = diffuseHeat;
    }



    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }
}
