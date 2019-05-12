package com.example.ecoapp;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView transactionList;
    private TextView headerView;
    private ArrayList<String> transactions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        db = new DatabaseHelper(this);
        transactions = new ArrayList<>();
        Cursor res = db.getAllData();
        while(res.moveToNext()){
            String dateString = res.getString(0);
            String[] splitString = dateString.split("T");
            String date = splitString[0];
            transactions.add("| " + date + " | " + res.getString(2) + " | " + res.getInt(1) + " | " + res.getString(3) + " |    ");
        }
        headerView = findViewById(R.id.headerView);
        headerView.setText("("+getString(R.string.date_header) +"|" +getString(R.string.type_header) +"|" +getString(R.string.value_header) +"|" +getString(R.string.partner_header) +")");
        transactionList = findViewById(R.id.transactionView);
        ArrayAdapter<String> transactionAdapter = new ArrayAdapter<>(this, R.layout.item_lv,R.id.listViewItem,transactions);
        transactionList.setAdapter(transactionAdapter);




    }


}
