package com.example;

public interface IThermoObservable {

    void attach(IThermoObserver thermoObserver);
    void NotifyThermoViewOfSystemAttributes(int time, double averageTemperature, double outsideTemperature, double cost); //methode de Object je suppose
    
}