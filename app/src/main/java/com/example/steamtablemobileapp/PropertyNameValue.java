package com.example.steamtablemobileapp;

public class PropertyNameValue {
    String propertyName;
    double propertyValue;

    public PropertyNameValue(String propertyName, double propertyValue){
        this.propertyName=propertyName;
        this.propertyValue=propertyValue;
    }


    public String getPropertyName() {
        return propertyName;
    }

    public double getPropertyValue() {
        return propertyValue;
    }
}
