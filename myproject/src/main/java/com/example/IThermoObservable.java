package com.example;

public interface IThermoObservable {

    void attach(IThermoObserver thermoObserver);
    void detach(IThermoObserver thermoObserver);
    void NotifyThermoViewOfSystemAttributes(int time, double averageTemperature, double outsideTemperature); //methode de Object je suppose
    
}