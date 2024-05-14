package com.example;

public interface IThermoObserver{
    //Timeline, temp moyenne, temp exterieure
    void updateSystemAttributes(int time, double averageTemperature, double exteriorTemperature, double cost);
}