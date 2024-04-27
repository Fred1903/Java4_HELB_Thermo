package com.example;

public interface ICellObservable {

    void attachCellObserver(ICellObserver cellObserver);
    void detachCellObserver(ICellObserver cellObserver);
<<<<<<< HEAD
    void NotifyThermoView(int row, int col, boolean isCellDead); 
=======
    void NotifyThermoView(int row, int col); //methode de Object je suppose
>>>>>>> db61e8ac2c49f2eaada66b0435a14931b4b7b2f3
    
}