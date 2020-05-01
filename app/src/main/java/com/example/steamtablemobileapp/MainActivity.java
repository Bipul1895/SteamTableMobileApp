package com.example.steamtablemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private List<Water> waterDataList= new ArrayList<>();
    private Spinner spinner1;
    private Spinner spinner2;
    private String field1,field2;//name of properties
    private EditText etField1, etField2;//two values provided by the user
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Method to read from csv file
        readWaterData();

        field1=getString(R.string.pressure);
        field2=getString(R.string.enthalpy);

        spinner1=findViewById(R.id.property1_spinner);
        spinner2=findViewById(R.id.property2_spinner);

        submitButton=findViewById(R.id.submit_btn);

        //Adapter for spinner1
        ArrayAdapter<CharSequence> adapter1=ArrayAdapter.createFromResource(this,R.array.spinner1,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(this);

        //Adapter for spinner2
        ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(this,R.array.spinner2,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);

        etField1=findViewById(R.id.press_temp_et);
        etField2=findViewById(R.id.second_property_et);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String valFieldStr1=etField1.getText().toString().trim();
                String valFieldStr2=etField2.getText().toString().trim();

                if(valFieldStr1.isEmpty() || valFieldStr2.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter both values", Toast.LENGTH_SHORT).show();
                    return;
                }

                double valField1=Double.parseDouble(valFieldStr1);
                double valField2=Double.parseDouble(valFieldStr2);

                Water foundRow=searchForDataInWaterDataList(field1,valField1);

                Log.d("Enthalpy", String.format("%f", foundRow.getEnthalpy_liquid()));
                Log.d("Enthalpy", String.format("%f", foundRow.getEnthalpy_gas()));

                Log.d("Entropy", String.format("%f", foundRow.getEntropy_liquid()));
                Log.d("Entropy", String.format("%f", foundRow.getEntropy_gas()));

                WaterState waterState=solveForWaterProperties(foundRow, field2, valField2);

                Intent intent=new Intent(MainActivity.this, Result.class);
                intent.putExtra("resultObject", waterState);
                startActivity(intent);

            }
        });
    }

    private WaterState solveForWaterProperties(Water foundRow, String field2, double valField2) {
        double quality;

        if(field2.equals(R.string.enthalpy)){
            quality=(valField2-foundRow.getEnthalpy_liquid())/(foundRow.getEnthalpy_gas()-foundRow.getEnthalpy_liquid());
        }
        else if(field2.equals(R.string.entropy)){
            quality=(valField2-foundRow.getEntropy_liquid())/(foundRow.getEntropy_gas()-foundRow.getEntropy_liquid());
        }
        else if(field2.equals(R.string.quality)){
            quality=valField2;
        }
        else if(field2.equals(R.string.specific_volume)){
            quality=(valField2-foundRow.getSpecific_volume_liquid())/(foundRow.getSpecific_volume_gas()-foundRow.getSpecific_volume_liquid());
        }
        else {
            quality=(valField2-foundRow.getInternal_energy_liquid())/(foundRow.getInternal_energy_gas()-foundRow.getInternal_energy_liquid());
        }

        double pressure=foundRow.getPressure();
        double temperature=foundRow.getTemperature();

        double enthalpy=foundRow.getEnthalpy_liquid() + quality*(foundRow.getEnthalpy_gas()-foundRow.getEnthalpy_liquid());
        double entropy=foundRow.getEntropy_liquid() + quality*(foundRow.getEntropy_gas()-foundRow.getEntropy_liquid());
        double spec_volume=foundRow.getSpecific_volume_liquid() + quality*(foundRow.getSpecific_volume_gas()-foundRow.getSpecific_volume_liquid());
        double internal_energy=foundRow.getInternal_energy_liquid() + quality*(foundRow.getInternal_energy_gas()-foundRow.getInternal_energy_liquid());

        return new WaterState(pressure,temperature,spec_volume,internal_energy,enthalpy,entropy);

    }

    private Water searchForDataInWaterDataList(String field1, double valField1) {

        Log.d("searchForDataIn : ", field1);

        boolean found=false;
        int foundIndex=-1;
        int lowIndex=-1,highIndex=-1;

        if(field1.equals("Pressure")){
            for(int i=0;i<waterDataList.size();i++){
                if(waterDataList.get(i).getPressure() == valField1){
                    found=true;
                    foundIndex=i;
                    break;
                }
                else if(waterDataList.get(i).getPressure() < valField1){
                    lowIndex=i;
                }
                else{//exceeded implies "not found". Hence, go for interpolation
                    highIndex=i;
                    break;
                }
            }
        }
        else if(field1.equals("Temperature")){
            for(int i=0;i<waterDataList.size();i++){
                if(waterDataList.get(i).getTemperature() == valField1){
                    found=true;
                    foundIndex=i;
                    break;
                }
                else if(waterDataList.get(i).getTemperature() < valField1){
                    lowIndex=i;
                }
                else{//exceeded implies "not found". Hence, go for interpolation
                    highIndex=i;
                    break;
                }
            }
        }

        Water rowObjectFound;

        if(!found){
            rowObjectFound=interpolateEntireRow(field1, valField1, waterDataList.get(lowIndex), waterDataList.get(highIndex));
        }
        else{
            rowObjectFound=waterDataList.get(foundIndex);
        }

        return  rowObjectFound;

    }

    private Water interpolateEntireRow(String field, double waterActual,Water waterLow, Water waterHigh) {
        Water waterInterpolatedRow;
        if(field.equals("Pressure")){
            waterInterpolatedRow=new Water(
                    waterActual,
                    interpolate(waterActual,waterLow.getTemperature(), waterHigh.getTemperature(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getSpecific_volume_liquid(), waterHigh.getSpecific_volume_liquid(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getSpecific_volume_gas(), waterHigh.getSpecific_volume_gas(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getInternal_energy_liquid(), waterHigh.getInternal_energy_liquid(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getInternal_energy_gas(), waterHigh.getInternal_energy_gas(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getEnthalpy_liquid(), waterHigh.getEnthalpy_liquid(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getEnthalpy_gas(), waterHigh.getEnthalpy_gas(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getEntropy_liquid(), waterHigh.getEntropy_liquid(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getEntropy_gas(), waterHigh.getEntropy_gas(), waterLow.getPressure(), waterHigh.getPressure())
                    );
        }
        else {
            waterInterpolatedRow=new Water(
                    interpolate(waterActual,waterLow.getTemperature(), waterHigh.getTemperature(), waterLow.getTemperature(), waterHigh.getTemperature()),
                    waterActual,
                    interpolate(waterActual,waterLow.getSpecific_volume_liquid(), waterHigh.getSpecific_volume_liquid(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getSpecific_volume_gas(), waterHigh.getSpecific_volume_gas(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getInternal_energy_liquid(), waterHigh.getInternal_energy_liquid(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getInternal_energy_gas(), waterHigh.getInternal_energy_gas(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getEnthalpy_liquid(), waterHigh.getEnthalpy_liquid(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getEnthalpy_gas(), waterHigh.getEnthalpy_gas(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getEntropy_liquid(), waterHigh.getEntropy_liquid(), waterLow.getPressure(), waterHigh.getPressure()),
                    interpolate(waterActual,waterLow.getEntropy_gas(), waterHigh.getEntropy_gas(), waterLow.getPressure(), waterHigh.getPressure())
            );
        }

        return waterInterpolatedRow;

    }

    private double interpolate(double x, double y1, double y2, double x1, double x2) {
        return (y2-y1)*(x-x1)/(x2-x1) + y1;
    }

    //Retrieve data from csv file and put it into "waterDataList" (ArrayList of Water class)
    private void readWaterData() {
        InputStream inputStream=getResources().openRawResource(R.raw.steamtable_data);
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        String line;

        try {
            //1st line contains name of the columns, not useful
            line=reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            while ((line=reader.readLine())!=null){
                //split line using ","
                String[] token=line.split(",");

                double pressure=Double.parseDouble(token[0]);
                double temperature=Double.parseDouble(token[1]);
                double specific_volume_liquid=Double.parseDouble(token[2]);
                double specific_volume_gas=Double.parseDouble(token[3]);
                double internal_energy_liquid=Double.parseDouble(token[4]);
                double internal_energy_gas=Double.parseDouble(token[5]);
                double enthalpy_liquid=Double.parseDouble(token[6]);
                double enthalpy_gas=Double.parseDouble(token[8]);
                double entropy_liquid=Double.parseDouble(token[9]);
                double entropy_gas=Double.parseDouble(token[10]);

                Water waterData=new Water(pressure,temperature,specific_volume_liquid,specific_volume_gas,internal_energy_liquid,internal_energy_gas,enthalpy_liquid,enthalpy_gas,entropy_liquid,entropy_gas);

                waterDataList.add(waterData);
            }
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("onitemSelected : " , "method called");
        String text=adapterView.getItemAtPosition(i).toString();//name of property
        if(view == spinner1){
            field1=text;
        }
        else if(view == spinner2){
            field2=text;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
