package com.example;

public class CellFactory {
    private Cell cell;
    private final int NUMBER_ROWS = ThermoController.getNumberRows();
    private final int NUMBER_COLUMNS = ThermoController.getNumberColumns();

    private int probabiltyToBeDeadCell;
    private int maxDeadCell = NUMBER_ROWS+NUMBER_COLUMNS;
    private int minDeadCell = 0;
    private int distance ;

    private final int[][] heatSources = ThermoController.getStartHeatSources();

    public CellFactory(Cell cell){
        this.cell = cell;
    }


    public  boolean isCellDead(int row, int col){
        //Si le random = 15 alors cellule morte
        //Si source de chaleur proche alors modifie le minimum -2 pour chaque source proche, comme ca moins de chance d etre morte
        
        int shortestDistanceToHeatCell = getShortestDistanceToHeatCell(row,col);
        
        for(int i=1; i<=shortestDistanceToHeatCell;i++){//on commence a 1 pck sinon un valeur en trop
            minDeadCell ++; //plus une cellule est éloignée d'une source de chaleur, plus l'écart du random devient petit pour que plus de chance d etre morte
        }

        probabiltyToBeDeadCell = (int)(Math.random()*(maxDeadCell-minDeadCell+1)+minDeadCell);
        if(probabiltyToBeDeadCell==maxDeadCell){
            cell.setDead(true);  //on set la cellule a Dead, ce n'est pas a la cellFactory de dire au controller si cellule est morte ou pas ??
            return true;
        }
        
        return false;
    }

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


    private int calculateManhattanDistance(int rowCell, int colCell, int rowHeatCell, int colHeatCell) { 
        return Math.abs(rowCell - rowHeatCell) + Math.abs(colCell - colHeatCell);
    }

     
}
