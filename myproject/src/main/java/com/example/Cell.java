package com.example;

import java.util.HashMap;


public class Cell implements ICellObservable{
    private double temperature; 

    private boolean diffuseHeat=false;
    private boolean isDead=false;

    ICellObserver cellObserver;


    public void calculateCellTemperature(int[][] ADJACENT_ITEMS_MATRIX, double outsideTemperature,int row, int col, HashMap<String, Cell> cellMap){
            if(!diffuseHeat){
                //La 9ieme temperature est celle de la cellule meme qu'on peut directement recuperer dans la classe cell
                double [] adjacentItemsTemperatures=new double[ADJACENT_ITEMS_MATRIX.length];//nombre de temperatures de cases adjacentes d'une cellule
                for(int adjacentItem=0; adjacentItem<adjacentItemsTemperatures.length;adjacentItem++){
                    int rowOfAdjacentItem = ADJACENT_ITEMS_MATRIX[adjacentItem][0];
                    int colOfadjacentItem = ADJACENT_ITEMS_MATRIX [adjacentItem][1];
                    /// rowOfAdjacentItem n'est pas son row mais son row par rapport a notre row
                    adjacentItemsTemperatures[adjacentItem]=getTemperatureOfAdjacentItem((row+rowOfAdjacentItem), (col+colOfadjacentItem), outsideTemperature, cellMap);
                }
                calculateTemperature(adjacentItemsTemperatures);   
                        
                NotifyThermoView(row, col,diffuseHeat, temperature); //on notifie la temperature de la cellule
            }
    }
    
    private double getTemperatureOfAdjacentItem(int row, int col, double outsideTemperature, HashMap<String, Cell> cellMap){
        Cell cellNextTo = cellMap.get(ThermoController.getCellId(row, col));
        //si l'id de la case est dans la hashmap alors c'est une cellule et on renvoie sa temperature, sinon c'est a l'exterieur et on renvoie temp ext.
        if(cellNextTo != null){
            return cellNextTo.getTemperature();
        }
        else{
            return outsideTemperature;
        }
    }

    private void calculateTemperature(double [] adjacentItemsTemperatures){
        double totalTemperature = temperature;
        int totalNumberInfluences = 1; //la cellule elle meme
        for (double temperatureItem : adjacentItemsTemperatures) {
            if(temperatureItem!=ThermoController.getDeadCellNoTemperature()){ //si la temperature = -500 cela signifie que c'est une cellule morte et on n'en tient pas en comtpe
                totalTemperature += temperatureItem;
                totalNumberInfluences++;
            }
        }
        temperature = totalTemperature/totalNumberInfluences; //temperature = moyenne de tt les temperatures adjacentes + sa propre temp.
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

    @Override
    public void NotifyThermoView(int row, int col, boolean isHeatCell, double cellTemperature) {
        cellObserver.updateCellColor(row,col,isHeatCell,cellTemperature);
    }

    @Override
    public void attachCellObserver(ICellObserver cellObserver) {
        this.cellObserver = cellObserver;
        
    }

    @Override
    public void detachCellObserver(ICellObserver cellObserver) {
        // TODO Auto-generated method stub
        
    }
}
