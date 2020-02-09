package com.example.ecoapp;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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

        if(partnerValueHashMap.size() > 8){
            HashMap<String, Integer> sortedMap = sortByValue(partnerValueHashMap);
            int counter = 0;
            int other = 0;
            for (Map.Entry<String, Integer> entry: sortedMap.entrySet()){
                if (counter<8){
                pieData.add(new SliceValue(entry.getValue(),Color.RED).setLabel(entry.getValue() + "Ft-" + entry.getKey()));
                counter++;
                Log.i("SORTED", entry.getKey() + ": " + entry.getValue() + " Ft");
                } else {
                    other += entry.getValue();
                }

            }
            pieData.add(new SliceValue(other,Color.RED).setLabel(other + "Ft-" + "Other"));

        } else {
            for (Map.Entry<String, Integer> entry: partnerValueHashMap.entrySet()){
                pieData.add(new SliceValue(entry.getValue(),Color.RED).setLabel(entry.getValue() + "Ft-" + entry.getKey()));

            }
        }


        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);

        pieChartView.setPieChartData(pieChartData);

    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) //https://www.geeksforgeeks.org/sorting-a-hashmap-according-to-values/
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue())*-1;
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

}
