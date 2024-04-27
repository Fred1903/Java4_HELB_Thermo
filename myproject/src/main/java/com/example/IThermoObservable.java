package com.example;

public interface IThermoObservable {

    void attach(IThermoObserver thermoObserver);
    void detach(IThermoObserver thermoObserver);
    void Notify(); //methode de Object je suppose
    
}