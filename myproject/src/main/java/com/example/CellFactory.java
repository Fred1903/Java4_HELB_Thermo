package com.example;

import java.util.HashMap;

public class CellFactory { /////////est-ce que factory doit etre en static ??
    private Cell cell;

    private final int startMinDeadCell=0;
    private final int ROW_POSITION_IN_MATRIX = 0;
    private final int COL_POSITION_IN_MATRIX = 1;
    private final int FIRST_ROW_COL = 0;
    private final int LASTROW = ThermoController.getNumberRows()-1;
    private final int LASTCOLUMN = ThermoController.getNumberColumns()-1;
    private int probabiltyToBeDeadCell;
    private int maxDeadCell = ThermoController.getNumberRows()+ThermoController.getNumberColumns();
    private int minDeadCell = startMinDeadCell;
    private int distance ;
    private int middleRow;
    private int middleColumn;
    private int firstTemperature;

    private int[][] heatSources; 

    private HashMap<String,Cell> cellMap = new HashMap<String,Cell>();    

    
    public CellFactory(int firstTemperature){
        this.firstTemperature = firstTemperature;
    }

    //méthode calculant la distance entre deux cases
    private int calculateManhattanDistance(int rowCell, int colCell, int rowHeatCell, int colHeatCell) { 
        return Math.abs(rowCell - rowHeatCell) + Math.abs(colCell - colHeatCell);
    }

    //Regarde si la case qu'on a donné en paramètre est la case du milieu
    public boolean checkIfMiddle(int row, int col){
        if(LASTCOLUMN%2==0 && LASTROW%2==0){
            middleColumn= LASTCOLUMN/2;
            middleRow = LASTROW/2;
            if(row==middleRow && col == middleColumn)return true;
        }
        return false;
    }

    public void createCells(ThermoView thermoView){
        for (int row = 0; row < ThermoController.getNumberRows(); row++) {
            for (int col = 0; col < ThermoController.getNumberColumns(); col++) {
                Cell cell; //= new Cell();
                if(isCellEligibleToHeatSource(row,col)){//cas d'une source de chaleur
                    cell = new Cell(true,false,ThermoController.getHeatCellStartTemperature()); 
                }
                else{
                    if(isCellDead(row,col)){ //si cellule morte 
                        cell=new Cell(false,true,ThermoController.getDeadCellNoTemperature());
                    }
                    else{  //cas d'une cellule normale 
                        cell=new Cell(false,false,firstTemperature);
                    }
                }
                cell.attachCellObserver(thermoView); 
                String cellId = ThermoController.getCellId(row,col);
                cellMap.put(cellId,cell); //attention put pas add pour hashmap
                cell.NotifyThermoView(row, col,cell.isHeatCell(), cell.isHeatDiffuser(), cell.isCellDead(), cell.getTemperature()); //on notifie la temperature de la cellule
            }
        }
    }        

    public HashMap<String,Cell> getCellMap(){
        return cellMap;
    }

    //Méthode retournant la distance minimale entre une cellule et une source de chaleur
    private int getShortestDistanceToHeatCell(int rowCell, int colCell) {
        int shortestDistance = ThermoController.getMAXIMUM_NUMBER_ROWS_AND_COLUMNS(); // On met une grande valeur de base
        for (int[] heatSource : heatSources) {//pour chaque source de chaleur on reg la distance avec x,y
            int distance = calculateManhattanDistance(rowCell, colCell, heatSource[ROW_POSITION_IN_MATRIX], heatSource[COL_POSITION_IN_MATRIX]);
            if (distance < shortestDistance) {
                shortestDistance = distance; //pour chaque passage dans la boucle on verifie si la distance est plus petite que la distance plus petite qu'on avait avant
            }
        }
        return shortestDistance;
    }

    public boolean isCellDead(int row, int col){
        //Si le random = col+row alors cellule morte
        //Si source de chaleur éloignée alors modifie le minimum +1 pour chaque case entre, comme ca plus de chance d etre morte
        int shortestDistanceToHeatCell = getShortestDistanceToHeatCell(row,col);
        for(int i=1; i<=shortestDistanceToHeatCell;i++){//on commence a 1 pck sinon un valeur en trop
            minDeadCell ++; //plus une cellule est éloignée d'une source de chaleur, plus l'écart du random devient petit pour que plus de chance d etre morte
        }
        minDeadCell = startMinDeadCell;
        probabiltyToBeDeadCell = (int)(Math.random()*(maxDeadCell-minDeadCell+1)+minDeadCell);
        if(probabiltyToBeDeadCell==maxDeadCell)return true;
        return false;
    }

    //On verifie si la cellule est dans un des 4 coins ou au milieu, au milieu pas encore verif
    public boolean isCellEligibleToHeatSource(int row, int col){
        if((row == FIRST_ROW_COL && col == FIRST_ROW_COL) || (row==FIRST_ROW_COL && col == LASTCOLUMN) ||
            (row==LASTROW && col==FIRST_ROW_COL) || (row==LASTROW && col==LASTCOLUMN) || checkIfMiddle(row,col)){
                return true;
        }
        return false;
    }

    public void setHeatSources(int[][]heatSources){
        this.heatSources=heatSources;
    } 
}
