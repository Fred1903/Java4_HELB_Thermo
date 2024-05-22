package com.example;

public interface ICellObserver {
    void updateCellAttributes(int row, int col, boolean isHeatCell, boolean isHeatDiffuser, boolean isDeadCell, double cellTemperature);
}
