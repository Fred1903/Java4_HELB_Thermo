package com.example;

public class Cell {
    private int temperature; 

    private boolean diffuseHeat=false;
    private boolean isDead=false;
    


    public int getTemperature() {
        return temperature;
    }



    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }



    public boolean iHeatDiffuser() { //Attentions aux noms!!
        return diffuseHeat;
    }



    public void setDiffuseHeat(boolean diffuseHeat) {
        this.diffuseHeat = diffuseHeat;
    }



    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }



    public boolean isCellDead(){ //cellule morte "getter"
        return isDead;
    }

    public boolean isDead() { //2 fois le meme°
        return isDead;
    }
}
