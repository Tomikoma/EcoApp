package com.example.ecoapp;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements SetUsernameFragment.EditNameDialogListener{

    private BroadcastReceiver br;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private static final String SHARED_PREFS = "SsharedPrefs";
    public static final String CREDIT = "Jóvárírás";
    public static final String SHOPPING = "Vásárlás kártyával";
    public static final String PICKUP = "Készpénzfelvétel";


    private TextView nameView;
    private TextView phoneView;
    private boolean isNameSet = false;

    Context ctx;

    private Context getCtx(){
        return this.ctx;
    }

    private static String phoneNumber;
    private static String name;
    private static DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.nameView = findViewById(R.id.nameView);
        this.phoneView = findViewById(R.id.phoneView);
        db = new DatabaseHelper(this);


        nameView.setText("DefaultUsername");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)){

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }


        if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
            phoneNumber = savedInstanceState.getString("phoneNumber");
            name = savedInstanceState.getString("name");
            this.isNameSet = savedInstanceState.getBoolean("isNameSet");
            this.nameView.setText(name);
            this.phoneView.setText(phoneNumber);

        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        this.isNameSet =sharedPreferences.getBoolean("isNameSet",false);

        if(this.isNameSet){
            name = sharedPreferences.getString("username", "DefaultUser");
            phoneNumber = sharedPreferences.getString("phoneNumber", "123456789");
            this.nameView.setText(name);
            this.phoneView.setText(phoneNumber);

        } else {
            setUser();
        }
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        br = new SmsReceiver();
        registerReceiver(br,intentFilter);



    }


    public void launchStatistics(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void launchTransactions(View view){
        Intent intent = new Intent(this, TransactionsActivity.class);
        startActivity(intent);
    }

    public void getLatestTransactions(View view){//hiba kezeles
        Cursor res=null;
        int count = 0;
        String errmsg="";
        if(R.id.latest_shopping_btn == view.getId()) {
            res = db.getLastestTransaction("Vásárlás kártyával");
            count = res.getCount();
            errmsg = getString(R.string.no_shopping_error);
            res.moveToNext();
        } else if(R.id.latest_credit_btn == view.getId()){
            res = db.getLastestTransaction("Jóvárírás");
            count = res.getCount();
            errmsg = getString(R.string.no_credit_error);
            res.moveToNext();
        } else if(R.id.latest_cash_btn == view.getId()) {
            res = db.getLastestTransaction("Készpénzfelvétel");
            count = res.getCount();
            errmsg = getString(R.string.no_cashpickup_error);
            res.moveToNext();
        }
        if(count<1){
            DialogFragment errorFragment = new ErrorFragment();
            errorFragment.show(getSupportFragmentManager(),errmsg);
        } else {
            String dateString = res.getString(0);
            String[] splitString = dateString.split("T");
            String date = splitString[0];
            DialogFragment dialogFragment = LatestFragment.newInstance(date, res.getInt(1), res.getString(2));
            dialogFragment.show(getSupportFragmentManager(), "Latest");
        }
    }






    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
            {
                if(grantResults.length> 0 && grantResults[0] ==  PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Thank you for permitting!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Igy nem fog mukodni az applikacio.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setUser(){
        DialogFragment newFragment = SetUsernameFragment.newInstance("Felhasználónév beállítása"); //?
        newFragment.show(getSupportFragmentManager(),"Felhasználónév beállítása"); //?
    }

    @Override
    public void onFinishEditDialog(String username, String phone) {
        Toast.makeText(this, "Hi " + name, Toast.LENGTH_LONG).show();
        this.nameView.setText(username);
        this.phoneView.setText(phone);
        phoneNumber = phone;
        name = username;
        isNameSet = true;
        saveData();
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", name);
        editor.putString("phoneNumber",phoneNumber);
        editor.putBoolean("isNameSet",this.isNameSet);

        editor.apply();
        //Toast.makeText(this, "Data saved", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("username",this.nameView.getText().toString());
        outState.putString("phoneNumber",this.phoneView.getText().toString());
        outState.putBoolean("isNameSet",this.isNameSet);
        saveData();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);

        super.onDestroy();
    }

}
