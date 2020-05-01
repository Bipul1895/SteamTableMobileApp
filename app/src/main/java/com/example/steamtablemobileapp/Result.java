package com.example.steamtablemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class Result extends AppCompatActivity {

    private ListView resultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultListView = findViewById(R.id.result_list_view);

        Intent intent=getIntent();
        WaterState resultObject= intent.getParcelableExtra("resultObject");

        double pressure=resultObject.getPressure();
        double temperature=resultObject.getTemperature();
        double specificVolume=resultObject.getSpecific_volume();
        double internalEnergy=resultObject.getInternal_energy();
        double enthalpy=resultObject.getEnthalpy();
        double entropy=resultObject.getEntropy();

        ArrayList<PropertyNameValue> arrayResult = new ArrayList<>();
        arrayResult.add(new PropertyNameValue(getString(R.string.pressure), pressure));
        arrayResult.add(new PropertyNameValue(getString(R.string.temperature), temperature));
        arrayResult.add(new PropertyNameValue(getString(R.string.specific_volume), specificVolume));
        arrayResult.add(new PropertyNameValue(getString(R.string.internal_energy), internalEnergy));
        arrayResult.add(new PropertyNameValue(getString(R.string.enthalpy), enthalpy));
        arrayResult.add(new PropertyNameValue(getString(R.string.entropy), entropy));

        ResultListAdapter adapter=new ResultListAdapter(this, R.layout.adapter_list_view, arrayResult);
        resultListView.setAdapter(adapter);

    }
}
