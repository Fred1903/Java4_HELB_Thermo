package com.example;

public interface ICellObservable {

    void attachCellObserver(ICellObserver cellObserver);
    void NotifyThermoView(int row, int col, boolean isHeatCell, boolean isHeatDiffuser, boolean isDeadCell, double cellTemperature); 
    
}