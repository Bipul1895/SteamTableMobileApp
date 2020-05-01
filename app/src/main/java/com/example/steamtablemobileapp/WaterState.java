package com.example.steamtablemobileapp;

import android.os.Parcel;
import android.os.Parcelable;

public class WaterState implements Parcelable {
    private double pressure;
    private double temperature;
    private double specific_volume;
    private double internal_energy;
    private double enthalpy;
    private double entropy;

    public WaterState(double pressure, double temperature, double specific_volume, double internal_energy, double enthalpy, double entropy){
        this.pressure=pressure;
        this.temperature=temperature;
        this.specific_volume=specific_volume;
        this.internal_energy=internal_energy;
        this.enthalpy=enthalpy;
        this.entropy=entropy;
    }

    protected WaterState(Parcel in) {
        pressure = in.readDouble();
        temperature = in.readDouble();
        specific_volume = in.readDouble();
        internal_energy = in.readDouble();
        enthalpy = in.readDouble();
        entropy = in.readDouble();
    }

    public static final Creator<WaterState> CREATOR = new Creator<WaterState>() {
        @Override
        public WaterState createFromParcel(Parcel in) {
            return new WaterState(in);
        }

        @Override
        public WaterState[] newArray(int size) {
            return new WaterState[size];
        }
    };

    public double getPressure() {
        return pressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getSpecific_volume() {
        return specific_volume;
    }

    public double getInternal_energy() {
        return internal_energy;
    }

    public double getEnthalpy() {
        return enthalpy;
    }

    public double getEntropy() {
        return entropy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(pressure);
        parcel.writeDouble(temperature);
        parcel.writeDouble(specific_volume);
        parcel.writeDouble(internal_energy);
        parcel.writeDouble(enthalpy);
        parcel.writeDouble(entropy);
    }
}
