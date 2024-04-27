package com.example;

public class CellFactory {
    private Cell cell;
    private final int NUMBER_ROWS = ThermoController.getNumberRows();
    private final int NUMBER_COLUMNS = ThermoController.getNumberColumns();

    private int probabiltyToBeDeadCell;
    //private int maxDeadCell = 15; //doit varier par rapport au nombre de row/col
    private int maxDeadCell = NUMBER_ROWS+NUMBER_COLUMNS;
    private int minDeadCell = 0;
    //private int maxDistance = 3;  
    //Pas de distance maximale car on veut que plus une case est proche, moins de chance d etre morte, mais pas de distance max!
    private int distance ;

    //Cases avec sourcesChaleur  ---> idealement recup les sourcesChaleur de theSystem pour eviter couplage
    //avant il etait en static, a voir si pas remettre par la suite
    private final int[][] sources = {
        {0, 0}, {0, NUMBER_COLUMNS - 1}, {NUMBER_ROWS - 1, 0}, {NUMBER_ROWS - 1, NUMBER_COLUMNS - 1}, {NUMBER_ROWS / 2, NUMBER_COLUMNS / 2}
    };

    public CellFactory(Cell cell){
        this.cell = cell;
    }

    //NEARBY ? --> 5 et moins

    public  boolean isCellDead(/*boolean hasMiddle, inutile si on recupSources int numberHeatSourcesNearby*/ int row, int col){
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
        System.out.println(probabiltyToBeDeadCell);
        if(probabiltyToBeDeadCell==maxDeadCell){
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
