package com.example;

import java.util.HashMap;

import com.example.Cell;
import com.example.ThermoController;


public class AliveCell  {
    /* 

    public void calculateCellTemperature(int[][] ADJACENT_ITEMS_MATRIX, double outsideTemperature,int row, int col, HashMap<String, Cell> cellMap){
            if(!isHeatDiffuser){
                //La 9ieme temperature est celle de la cellule meme qu'on peut directement recuperer dans la classe cell
                double [] adjacentItemsTemperatures=new double[ADJACENT_ITEMS_MATRIX.length];//nombre de temperatures de cases adjacentes d'une cellule
                for(int adjacentItem=0; adjacentItem<adjacentItemsTemperatures.length;adjacentItem++){
                    int rowOfAdjacentItem = ADJACENT_ITEMS_MATRIX[adjacentItem][0];
                    int colOfadjacentItem = ADJACENT_ITEMS_MATRIX [adjacentItem][1];
                    /// rowOfAdjacentItem n'est pas son row mais son row par rapport a notre row
                    adjacentItemsTemperatures[adjacentItem]=getTemperatureOfAdjacentItem((row+rowOfAdjacentItem), (col+colOfadjacentItem), outsideTemperature, cellMap);
                }
                calculateTemperature(adjacentItemsTemperatures);   
            }
            NotifyThermoView(row, col,isHeatCell, isHeatDiffuser, temperature); //on notifie la temperature de la cellule
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
        return isHeatDiffuser;
    }

    public boolean isHeatCell(){
        return isHeatCell;
    }

    public void setHeatDiffuser(boolean isHeatDiffuser) {
        this.isHeatDiffuser = isHeatDiffuser;
        if(isHeatDiffuser){
            heatTemperatureBeforeDesactivating = temperature;
            setIsHeatCell(isHeatDiffuser); //si une cellule diffuse de la chaleur (elle est active), ce sera doffice une source de chaleur
            ////////////////////ICI A CHANGER LA TEMPERATURE A LA REACTIVATION !!!!!!!!!!!!!!!!!!
            temperature=ThermoController.getHeatCellStartTemperature() ; //quand on réactive une source de chaleur on remet ca temperature a celle de base
        }
        else{
            ///Fonctionne pas encore à 100%, sc qu'on créé prennent temp par defaut
            if(heatTemperatureBeforeDesactivating!=startHeatTemperatureBeforeDesactivating){
                temperature = heatTemperatureBeforeDesactivating;
            }
        }
    }

    public void setIsHeatCell(boolean isHeatCell){
        this.isHeatCell=isHeatCell;
        if(!isHeatCell){
            setHeatDiffuser(isHeatCell);
        }
    }*/
}
