package com.example;

public class CellFactory {
    private Cell cell;
    private final int NUMBER_ROWS = ThermoController.getNumberRows();
    private final int NUMBER_COLUMNS = ThermoController.getNumberColumns();

    private int probabiltyToBeDeadCell;
    private int maxDeadCell = NUMBER_ROWS+NUMBER_COLUMNS;
    private int minDeadCell = 0;
    //Pas de distance maximale car on veut que plus une case est proche, moins de chance d etre morte, mais pas de distance max!
    private int distance ;

    private final int[][] sources = ThermoController.getStartHeatSources();

    public CellFactory(Cell cell){
        this.cell = cell;
    }


    public  boolean isCellDead(int row, int col){
        //Si le random = 15 alors cellule morte
        //Si source de chaleur proche alors modifie le minimum -2 pour chaque source proche, comme ca moins de chance d etre morte
        

        /*boolean isClose = isSourceClose(row, col, maxDistance);
        if(isClose){ //si cellule est pres de la source de chaleur(par rapport a distanceMax), alors -2 par case proche pour augmenter le random
            //a voir si on enleve pas distance et on met juste -2 par case proche 
            for (int i = 1; i <= maxDistance; i++) {
                //if(distance == i) break;
                if(i<=distance){
                    minDeadCell -=2;
                }
                else{
                    break;
                }
            }
        }*/
        int shortestDistanceToHeatCell = getShortestDistanceToHeatCell(row,col);
        for(int i=1; i<=NUMBER_COLUMNS;i++) { //la j ai mis numbercol, mais faut regarder la distance maximum possible avec source chal
            if(i<=distance){
                minDeadCell -=2;
            }
            else{
                break;
            }
        }

        probabiltyToBeDeadCell = (int)(Math.random()*(maxDeadCell-minDeadCell+1)+minDeadCell);
        if(probabiltyToBeDeadCell==maxDeadCell){
            cell.setDead(true);  //on set la cellule a Dead, ce n'est pas a la cellFactory de dire au controller si cellule est morte ou pas ??
            return true;
        }
        
        return false;
    }
    //Avant en static
    /*private boolean isSourceClose(int x, int y, int maxDistance) {
        for (int[] source : sources) {
            if (calculateManhattanDistance(x, y, source[0], source[1]) <= maxDistance) {
                return true;
            }
        }
        return false;
    }*/

    private int getShortestDistanceToHeatCell(int x, int y) {
        int shortestDistance = Integer.MAX_VALUE; // Initialiser avec une valeur très grande

        for (int[] source : sources) {
            int distance = calculateManhattanDistance(x, y, source[0], source[1]);
            if (distance < shortestDistance) {
                shortestDistance = distance;
            }
        }
        return shortestDistance;
    }


    //Avant en static
    private int calculateManhattanDistance(int x1, int y1, int x2, int y2) {
        distance = Math.abs(x1 - x2) + Math.abs(y1 - y2); 
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

     
}
