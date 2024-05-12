package com.example;


public interface HeatCellStrategy{
    //void determineStrategy(boolean selectedManual, boolean selectedTarget);
    void applyStrategy(Cell cell);
}

class ManualStrategy implements HeatCellStrategy{//attention on ne peut pas mettre public devant, car sinon doit faire dans un autre fichier

    @Override
    public void applyStrategy(Cell cell) {
        cell.setHeatDiffuser(!cell.isHeatDiffuser());
    }

}

class TargetStrategy implements HeatCellStrategy{

    @Override
    public void applyStrategy(Cell cell) {
        
    }

}