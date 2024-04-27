package com.example;

public class Cell {
    private String color; //view
    private int temperature;

    private boolean diffuseHeat;
    private boolean isDead;
    


<<<<<<< HEAD
    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }



=======
>>>>>>> db61e8ac2c49f2eaada66b0435a14931b4b7b2f3
    public boolean isCellDead(){ //cellule morte
        return isDead;
    }
}
