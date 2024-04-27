package com.example;

public interface ICellObserver {
    void updateCellColor(int row, int col, boolean isHeatCell, int cellTemperature);
}
