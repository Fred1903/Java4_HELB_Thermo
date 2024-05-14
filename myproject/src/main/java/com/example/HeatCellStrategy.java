package com.example;

import java.util.HashMap;

public interface HeatCellStrategy{
    //void determineStrategy(boolean selectedManual, boolean selectedTarget);
    void applyStrategy(Cell cell, double averageTemperature, HashMap<String, Cell> heatCellMap, int uselessNumberAliveCells);
}

class ManualStrategy implements  HeatCellStrategy{//attention on ne peut pas mettre public devant, car sinon doit faire dans un autre fichier

    @Override
    public void applyStrategy(Cell cell, double uselessAverageTemperature, HashMap<String, Cell> uselessHeatCellMap, int numberAliveCells) {
        cell.setHeatDiffuser(!cell.isHeatDiffuser());
        System.out.println("Dans strategie");
    }

}

class TargetStrategy implements HeatCellStrategy{

    @Override 
    public void applyStrategy(Cell uselessCell, double averageTemperature, HashMap<String, Cell> heatCellMap, int numberAliveCells) {
        /*System.out.println("Dans target");
        Cell cello = cellMap.get("R1C2");
        cello.setHeatDiffuser(true);*/
        //return cellMap;
        boolean allHeatCellsActivated=false;
        boolean allHeatCellsDesactivated=false;

        if(averageTemperature>20){
            while(!allHeatCellsDesactivated && averageTemperature>20){
                for (Cell cell : heatCellMap.values()) {
                    double cellTemperature = cell.getTemperature();
                    double totalTemperatureWithoutThisCell = averageTemperature*numberAliveCells-cellTemperature;
                    int numberAliveCellsWithoutThisCell = numberAliveCells-1;
                    averageTemperature = totalTemperatureWithoutThisCell/numberAliveCellsWithoutThisCell;
                    cell.setHeatDiffuser(false);
                    System.out.println("New average temp :"+averageTemperature);
                } ///numberAlives cells et average temp pas mis a jour dans controller !!
                allHeatCellsDesactivated=true;
            }
        }
        else if(averageTemperature<20){
            while(!allHeatCellsActivated && averageTemperature<20){
                for (Cell cell : heatCellMap.values()) {
                    cell.setHeatDiffuser(true);
                    double cellTemperature = cell.getTemperature();
                    double totalTemperatureWithoutThisCell = averageTemperature*numberAliveCells-cellTemperature;
                    //numberAliveCellsWithoutThisCell = numberAliveCells-1;
                    averageTemperature = (totalTemperatureWithoutThisCell+cellTemperature)/numberAliveCells;
                    
                    System.out.println("New average temp :"+averageTemperature);
                } ///numberAlives cells et average temp pas mis a jour dans controller !!
                allHeatCellsActivated=true;
            }
        }
    }

}