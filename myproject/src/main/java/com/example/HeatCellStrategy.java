package com.example;


public interface HeatCellStrategy{
    void determineStrategy(boolean selectedManual, boolean selectedSuccessive);
}

class ManualStrategy implements HeatCellStrategy{//attention on ne peut pas mettre public devant, car sinon doit faire dans un autre fichier

    @Override
    public void determineStrategy(boolean selectedManual, boolean selectedSuccessive) {
        
    }

}

class SuccessiveStrategy implements HeatCellStrategy{

    @Override
    public void determineStrategy(boolean selectedManual, boolean selectedSuccessive) {
        
    }

}