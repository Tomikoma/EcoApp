package com.example.ecoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity {

    DatabaseHelper db;
    private TextView numberView;
    private TextView totalShoppingView;
    private TextView avgShoppingView;
    private TextView totalCreditView;
    private TextView avgCreditView;
    private TextView totalPickupView;
    private TextView avgPickupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        db = new DatabaseHelper(this);
        this.numberView = findViewById(R.id.numberView);
        this.totalShoppingView = findViewById(R.id.totalShoppingView);
        this.avgShoppingView = findViewById(R.id.avgShoppingView);
        this.totalCreditView = findViewById(R.id.totalCreditView);
        this.avgCreditView = findViewById(R.id.avgCreditView);
        this.totalPickupView = findViewById(R.id.totalPickupView);
        this.avgPickupView = findViewById(R.id.avgPickupView);


        this.numberView.setText(getString(R.string.nbr_trans_tv) + " " + db.getAmountOfTransactions());
        this.totalShoppingView.setText(getString(R.string.total_shop_tv) + " " + db.getTotalValueOfType(MainActivity.SHOPPING));
        this.avgShoppingView.setText(getString(R.string.avg_shop_tv) + " " + db.getAvgValueOfType(MainActivity.SHOPPING));
        this.totalCreditView.setText(getString(R.string.total_credit_tv) + " " + db.getTotalValueOfType(MainActivity.CREDIT));
        this.avgCreditView.setText(getString(R.string.avg_credit_tv) + " " + db.getAvgValueOfType(MainActivity.CREDIT));
        this.totalPickupView.setText(getString(R.string.total_pickup_tv) + " " + db.getTotalValueOfType(MainActivity.PICKUP));
        this.avgPickupView.setText(getString(R.string.avg_pickup_tv) + " " + db.getAvgValueOfType(MainActivity.PICKUP));
    }
}
