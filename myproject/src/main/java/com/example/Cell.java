package com.example;

import java.util.HashMap;

public class Cell implements ICellObservable{
    private double temperature; 

    private boolean isHeatDiffuser=false;
    private boolean isHeatCell=false;

    private boolean isDead=false;

    ICellObserver cellObserver;

    public Cell(){}

    public Cell(boolean isHeatDiffuser, boolean isDead, double temperature){
        this.isHeatDiffuser=isHeatDiffuser;
        this.isDead=isDead;
        this.temperature=temperature;

        if(this.isHeatDiffuser){ 
            setIsHeatCell(isHeatDiffuser); //si une cellule diffuse de la chaleur (elle est active), ce sera doffice une source de chaleur
            temperature=ThermoController.getHeatCellStartTemperature() ; //quand on réactive une source de chaleur on remet ca temperature a celle de base
        }
        
    }

    public void calculateCellTemperature(int[][] ADJACENT_ITEMS_MATRIX, double outsideTemperature,int row, int col, HashMap<String, Cell> cellMap){
            if(!isHeatDiffuser && !isDead){
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
            NotifyThermoView(row, col,isHeatCell, isHeatDiffuser, isDead, temperature); //on notifie la temperature de la cellule
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

    public boolean isCellDead(){ //cellule morte "getter"
        return isDead;
    }

    public void setHeatDiffuser(boolean isHeatDiffuser) {
        this.isHeatDiffuser = isHeatDiffuser;
        if(isHeatDiffuser){
            if(isDead)setDead(!isHeatDiffuser);//si c'etait cellule morte alors plus mtn
            setIsHeatCell(isHeatDiffuser); //si une cellule diffuse de la chaleur (elle est active), ce sera doffice une source de chaleur
            temperature=ThermoController.getHeatCellStartTemperature() ; //quand on réactive une source de chaleur on remet ca temperature a celle de base
        }
    }

    public void setIsHeatCell(boolean isHeatCell){
        this.isHeatCell=isHeatCell;
        if(!isHeatCell){
            setHeatDiffuser(isHeatCell);
        }
    }

    

    public void setDead(boolean isDead) {
        this.isDead = isDead;
        if(isDead){
            temperature=ThermoController.getDeadCellNoTemperature();
            setHeatDiffuser(!isDead);
            setIsHeatCell(!isDead);
        }
        else{
            setTemperature(0);//premiere temp du fichier !!!!!
        }
    }


    public int updateCell(boolean isClickedOnDeadCell, boolean isClickedOnHeatCell, double choiceTemperature){
        int numberAliveCellsChanges=0;
        if(isDead){
            if(!isClickedOnDeadCell){
                setDead(!isCellDead());
                numberAliveCellsChanges++;
            } 
        }
        else{
            if(isClickedOnDeadCell){ 
                numberAliveCellsChanges--;
                setDead(!isDead);
            }
        }
        if(isHeatCell){
            if(!isClickedOnHeatCell){
                setIsHeatCell(!isHeatCell);
            }
            else{
                if(choiceTemperature!=temperature)setTemperature(choiceTemperature);
            }
        }
        else{
            if(isClickedOnHeatCell){//si on a click sur sc alors on la definit comme sc et on met a jour sa temp
                if(!isHeatCell){
                    //on pourrait faire les 2 lignes ci-dessous sans le if mais alors on va aussi faire pour des sources de chaleur et donc gaspillage de ressources
                    setHeatDiffuser(isClickedOnHeatCell);
                    setTemperature(choiceTemperature);
                } 
            }
        } 
        return numberAliveCellsChanges;
    }

    @Override
    public void NotifyThermoView(int row, int col, boolean isHeatCell, boolean isHeatDiffuser,  boolean isDeadCell, double cellTemperature) {
        cellObserver.updateCellColor(row,col,isHeatCell, isHeatDiffuser, isDeadCell, cellTemperature);
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
