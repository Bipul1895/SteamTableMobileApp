package com.example.steamtablemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Method to read from csv file
        readWaterData();

        spinner1=findViewById(R.id.property1_spinner);
        spinner2=findViewById(R.id.property2_spinner);

        etField1=findViewById(R.id.press_temp_et);
        etField2=findViewById(R.id.second_property_et);

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

        double valField1=Double.parseDouble(etField1.getText().toString());
        double valField2=Double.parseDouble(etField2.getText().toString());

        searchForDataInWaterDataList(field1,valField1,field2,valField2);


    }

    private void searchForDataInWaterDataList(String field1, double valField1, String field2, double valField2) {

        if(field1.equals("Pressure")){
            
        }
        else if(field1.equals("Temperature")){

        }

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
                double enthalpy_gas=Double.parseDouble(token[7]);
                double entropy_liquid=Double.parseDouble(token[8]);
                double entropy_gas=Double.parseDouble(token[9]);

                Water waterData=new Water(pressure,temperature,specific_volume_liquid,specific_volume_gas,internal_energy_liquid,internal_energy_gas,enthalpy_liquid,enthalpy_gas,entropy_liquid,entropy_gas);

                waterDataList.add(waterData);
            }
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
