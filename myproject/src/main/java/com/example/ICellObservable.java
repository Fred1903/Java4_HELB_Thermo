package com.example;

public interface ICellObservable {

    void attachCellObserver(ICellObserver cellObserver);
    void detachCellObserver(ICellObserver cellObserver);
    void NotifyThermoView(int row, int col, boolean isHeatCell, double cellTemperature); 
    
}