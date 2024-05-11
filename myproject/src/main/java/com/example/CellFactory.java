package com.example;

import java.util.HashMap;

public class CellFactory { /////////est-ce que factory doit etre en static ??
    private Cell cell;
    private final int NUMBER_ROWS = ThermoController.getNumberRows();
    private final int NUMBER_COLUMNS = ThermoController.getNumberColumns();

    private int probabiltyToBeDeadCell;
    private int maxDeadCell = NUMBER_ROWS+NUMBER_COLUMNS;
    private int minDeadCell = 0;
    private int distance ;


    private final int FIRST_ROW_COL = 0;
    private final int MINIMUM_NUMBER_ROWS_AND_COLUMNS = 3;
    private final int MAXIMUM_NUMBER_ROWS_AND_COLUMNS = 12;
    
    private final int LASTROW = NUMBER_ROWS-1;
    private final int LASTCOLUMN = NUMBER_COLUMNS-1;
    private final int HEAT_CELL_START_TEMPERATURE = 18; 
    private int middleRow;
    private int middleColumn;

    private HashMap<String,Cell> cellMap = new HashMap<String,Cell>();

    //private ThermoController thermoController = new ThermoController(null);

    private int[][] heatSources; //ThermoController.getStartHeatSources();
    

    public void setHeatSources(int[][]heatSources){
        this.heatSources=heatSources;
    }
    /*public CellFactory(Cell cell){
        this.cell = cell;
    }*/

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
        for (int row = 0; row < NUMBER_ROWS; row++) {
            for (int col = 0; col < NUMBER_COLUMNS; col++) {
                Cell cell; //= new Cell();
                if(isCellEligibleToHeatSource(row,col)){
                    // au lieu de mettre true on peut mettre isCellEligibible(row,col) vu que return bool 
                    //cas d'une source de chaleur
                    cell = new Cell(true,false,HEAT_CELL_START_TEMPERATURE); 
                }
                else{
                    //cellFactory= new CellFactory(cell);
                    if(isCellDead(row,col)){ //si cellule morte 
                        //-500 a enlever pour utiliser juste le boolean
                        cell=new Cell(false,true,-500);  //A LA PLACE DE 0 on doit mettre la 1ere temperature du fichier
                    }
                    else{
                        cell=new Cell(false,false,0); //cas d'une cellule normale 
                    }
                }
                cell.attachCellObserver(thermoView); //a faire dans le ctrl ?
                String cellId = ThermoController.getCellId(row,col);
                cellMap.put(cellId,cell); //attention put pas add pour hashmap
                cell.NotifyThermoView(row, col,cell.isHeatCell(), cell.isHeatDiffuser(), cell.getTemperature()); //on notifie la temperature de la cellule
            }
        }
    }

    public HashMap<String,Cell> getCellMap(){
        return cellMap;
    }

    //On verifie si la cellule est dans un des 4 coins ou au milieu, au milieu pas encore verif
    public boolean isCellEligibleToHeatSource(int row, int col){
        //Est-ce que c'est considéré comme de la répétition de code ?
        if((row == FIRST_ROW_COL && col == FIRST_ROW_COL) || (row==FIRST_ROW_COL && col == LASTCOLUMN) ||
            (row==LASTROW && col==FIRST_ROW_COL) || (row==LASTROW && col==LASTCOLUMN) || checkIfMiddle(row,col)){
                return true;
        }
        return false;
    }



    public boolean isCellDead(int row, int col){
        //Si le random = 15 alors cellule morte
        //Si source de chaleur proche alors modifie le minimum -2 pour chaque source proche, comme ca moins de chance d etre morte
        int shortestDistanceToHeatCell = getShortestDistanceToHeatCell(row,col);
        for(int i=1; i<=shortestDistanceToHeatCell;i++){//on commence a 1 pck sinon un valeur en trop
            minDeadCell ++; //plus une cellule est éloignée d'une source de chaleur, plus l'écart du random devient petit pour que plus de chance d etre morte
        }
        probabiltyToBeDeadCell = (int)(Math.random()*(maxDeadCell-minDeadCell+1)+minDeadCell);
        if(probabiltyToBeDeadCell==maxDeadCell){
            //cell.setDead(true);  //on set la cellule a Dead, ce n'est pas a la cellFactory de dire au controller si cellule est morte ou pas ??
            //cell.setDead a enlever car va dire dans le constructeur que dead
            return true;
        }
        return false;
    }

    //Méthode retournant la distance minimale entre une cellule et une source de chaleur
    private int getShortestDistanceToHeatCell(int rowCell, int colCell) {
        int shortestDistance = ThermoController.getMAXIMUM_NUMBER_ROWS_AND_COLUMNS(); // On met une grande valeur de base
        for (int[] heatSource : heatSources) {//pour chaque source de chaleur on reg la distance avec x,y
            int distance = calculateManhattanDistance(rowCell, colCell, heatSource[0], heatSource[1]);
            if (distance < shortestDistance) {
                shortestDistance = distance; //pour chaque passage dans la boucle on verifie si la distance est plus petite que la distance plus petite qu'on avait avant
            }
        }
        return shortestDistance;
    }

    //méthode calculant la distance entre deux cases
    private int calculateManhattanDistance(int rowCell, int colCell, int rowHeatCell, int colHeatCell) { 
        return Math.abs(rowCell - rowHeatCell) + Math.abs(colCell - colHeatCell);
    }

     
}
