package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface HeatCellStrategy{
    //void determineStrategy(boolean selectedManual, boolean selectedTarget);
    void applyStrategy(Cell cell, double averageTemperature, HashMap<String, Cell> heatCellMap, int uselessNumberAliveCells);
}

class ManualStrategy implements  HeatCellStrategy{//attention on ne peut pas mettre public devant, car sinon doit faire dans un autre fichier

    @Override
    public void applyStrategy(Cell cell, double uselessAverageTemperature, HashMap<String, Cell> uselessHeatCellMap, int numberAliveCells) {
        cell.setHeatDiffuser(!cell.isHeatDiffuser());
    }
}


class SuccesiveStrategy implements  HeatCellStrategy{
    private List<String> heatCellList = new ArrayList<String>();
    private int nextHeatCellToActivate = 0;
    private final int FIRST_INDEX_OF_LIST = 0;
    private Cell currentActivatedHeatCell;
    private boolean areHeatCellsDesactivatedAtStart=false;
    private int heatCellListLastIndex;
    @Override
    public void applyStrategy(Cell uselessCell, double uselessAverageTemperature, HashMap<String, Cell> heatCellMap, int numberAliveCells) {
        if(!areHeatCellsDesactivatedAtStart){
            System.out.println("DEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            for (Cell heatCell : heatCellMap.values()) { //au début de la stratégie on désactive toutes les sc
                heatCell.setHeatDiffuser(false);
                areHeatCellsDesactivatedAtStart=true;
            }
        }
        
        for (String heatCellId : heatCellMap.keySet()) { //On ajoute les sc qui sont pas dans la liste et on les desactive
            if(!heatCellList.contains(heatCellId)){
                heatCellList.add(heatCellId);
                Cell cellToDesactivate = heatCellMap.get(heatCellId);
                cellToDesactivate.setHeatDiffuser(false);
            }
        }
        
        List<String> heatCellsToRemove = new ArrayList<String>();
        for (String heatCellInList : heatCellList) {
            if (!heatCellMap.containsKey(heatCellInList)) {
                heatCellsToRemove.add(heatCellInList);
            }
        } //On ne peut pas supprimer directement dans le foreach car sinon ca provoque une erreur
        heatCellList.removeAll(heatCellsToRemove); 

        //on désactive d'abord la sc précédente si il y en a une 
        if(currentActivatedHeatCell != null) currentActivatedHeatCell.setHeatDiffuser(!currentActivatedHeatCell.isHeatDiffuser()); 
        heatCellListLastIndex = heatCellList.size()-1;
        if(nextHeatCellToActivate>heatCellListLastIndex) nextHeatCellToActivate = FIRST_INDEX_OF_LIST;
        currentActivatedHeatCell = heatCellMap.get(heatCellList.get(nextHeatCellToActivate));
        currentActivatedHeatCell.setHeatDiffuser(!currentActivatedHeatCell.isHeatDiffuser());//on active la prochaine sc
        nextHeatCellToActivate++;
    }
}


class TargetStrategy implements HeatCellStrategy{
    private double newAverageTemperature;
    private int newNumberAliveCells;
    private final int IDEAL_TEMPERATURE = 20;

    @Override 
    public void applyStrategy(Cell uselessCell, double averageTemperature, HashMap<String, Cell> heatCellMap, int numberAliveCells) {
        boolean allHeatCellsActivated=false;
        boolean allHeatCellsDesactivated=false;

        if(averageTemperature>IDEAL_TEMPERATURE){
            while(!allHeatCellsDesactivated && averageTemperature>IDEAL_TEMPERATURE){
                for (Cell cell : heatCellMap.values()) {
                    cell.setHeatDiffuser(false);

                    double cellTemperature = cell.getTemperature();
                    double totalTemperatureWithoutThisCell = averageTemperature*numberAliveCells-cellTemperature;
                    //on enleve la temperature de la cellule pour voir cmb fait la moyenne sans celle-ci (probleme on devrait faire sa moyenne À elle alors ?)
                    int numberAliveCellsWithoutThisCell = numberAliveCells-1;
                    averageTemperature = totalTemperatureWithoutThisCell/numberAliveCellsWithoutThisCell;
                    newNumberAliveCells=numberAliveCellsWithoutThisCell;
                    newAverageTemperature=averageTemperature;
                } 
                allHeatCellsDesactivated=true;
            }
        }
        else if(averageTemperature<IDEAL_TEMPERATURE){
            while(!allHeatCellsActivated && averageTemperature<IDEAL_TEMPERATURE){
                for (Cell cell : heatCellMap.values()) {
                    cell.setHeatDiffuser(true);

                    double cellTemperature = cell.getTemperature();
                    double totalTemperatureWithoutThisCell = averageTemperature*numberAliveCells-cellTemperature;

                    //numberAliveCellsWithoutThisCell = numberAliveCells-1;
                    averageTemperature = (totalTemperatureWithoutThisCell+cellTemperature)/numberAliveCells;
                    newNumberAliveCells=numberAliveCells;
                    newAverageTemperature=averageTemperature;
                }
                allHeatCellsActivated=true;
            }
        }
    }

    public double getNewAverageTemperature(){
        return newAverageTemperature;
    }
}