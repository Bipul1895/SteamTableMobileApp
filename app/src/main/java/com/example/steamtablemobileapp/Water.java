package com.example.steamtablemobileapp;

public class Water {
    private double pressure;
    private double temperature;
    private double specific_volume_liquid;
    private double specific_volume_gas;
    private double internal_energy_liquid;
    private double internal_energy_gas;
    private double enthalpy_liquid;
    private double enthalpy_gas;
    private double entropy_liquid;
    private double entropy_gas;

    public Water(double pressure, double temperature, double specific_volume_liquid, double specific_volume_gas, double internal_energy_liquid, double internal_energy_gas, double enthalpy_liquid, double enthalpy_gas, double entropy_liquid, double entropy_gas){
        this.pressure=pressure;
        this.temperature=temperature;
        this.specific_volume_liquid=specific_volume_liquid;
        this.specific_volume_gas=specific_volume_gas;
        this.internal_energy_liquid=internal_energy_liquid;
        this.internal_energy_gas=internal_energy_gas;
        this.enthalpy_liquid=enthalpy_liquid;
        this.enthalpy_gas=enthalpy_gas;
        this.entropy_liquid=entropy_liquid;
        this.entropy_gas=entropy_gas;
    }

    public double getPressure() {
        return pressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getSpecific_volume_liquid() {
        return specific_volume_liquid;
    }

    public double getSpecific_volume_gas() {
        return specific_volume_gas;
    }

    public double getInternal_energy_liquid() {
        return internal_energy_liquid;
    }

    public double getInternal_energy_gas() {
        return internal_energy_gas;
    }

    public double getEnthalpy_liquid() {
        return enthalpy_liquid;
    }

    public double getEnthalpy_gas() {
        return enthalpy_gas;
    }

    public double getEntropy_liquid() {
        return entropy_liquid;
    }

    public double getEntropy_gas() {
        return entropy_gas;
    }

}
