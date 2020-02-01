package com.example.ecoapp;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChartActivity extends AppCompatActivity {

    private PieChartView pieChartView;
    private DatabaseHelper db;
    private HashMap<String, Integer> partnerValueHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        db = new DatabaseHelper(this);
        String chartType = getIntent().getStringExtra("PIE_CHART_TYPE");
        partnerValueHashMap = new HashMap<>();
        pieChartView = findViewById(R.id.pieChart);
        List<SliceValue> pieData = new ArrayList<>();

        Cursor dataCursor = db.getAllData();
        while(dataCursor.moveToNext()) {
            String type = dataCursor.getString(2);
            int value = dataCursor.getInt(1);
            String partner = dataCursor.getString(3);

            if(type != null && type.equals(chartType)){

                if(partnerValueHashMap.containsKey(partner))
                    partnerValueHashMap.put(partner,partnerValueHashMap.get(partner)+value);
                else
                    partnerValueHashMap.put(partner,value);
            }
        }

        for (Map.Entry<String, Integer> entry: partnerValueHashMap.entrySet()){
            pieData.add(new SliceValue(entry.getValue(),Color.RED).setLabel(entry.getValue() + "Ft-" + entry.getKey()));
        }
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);

        pieChartView.setPieChartData(pieChartData);

    }
}
