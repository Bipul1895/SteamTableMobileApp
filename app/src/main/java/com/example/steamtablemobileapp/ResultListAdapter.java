package com.example.steamtablemobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResultListAdapter extends ArrayAdapter<PropertyNameValue> {

    private Context mContext;
    private int mResource;

    public ResultListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<PropertyNameValue> objects) {
        super(context, resource, objects);
        mContext=context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get property information
        String propertyName=getItem(position).getPropertyName();
        double propertyValue=getItem(position).getPropertyValue();

        LayoutInflater inflater=(LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View rows=inflater.inflate(R.layout.adapter_list_view,parent,false);

        TextView etPropertyName=rows.findViewById(R.id.name_of_property_tv);
        TextView etPropertyValue=rows.findViewById(R.id.value_of_property_tv);

        etPropertyName.setText(propertyName);
        etPropertyValue.setText(String.format("%f", propertyValue));

        return rows;

    }
}
