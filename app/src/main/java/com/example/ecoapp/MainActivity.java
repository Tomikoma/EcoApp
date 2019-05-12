package com.example.ecoapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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

    private TextView nameView;
    private TextView phoneView;
    private static TextView balanceView;
    private boolean isNameSet = false;
    private static String phoneNumber;
    private static String name;
    private static int balance;
    private static DatabaseHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.nameView = findViewById(R.id.nameView);
        this.phoneView = findViewById(R.id.phoneView);
        balanceView = findViewById(R.id.balanceView);
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
            balance = savedInstanceState.getInt("balance");
            name = savedInstanceState.getString("name");
            this.isNameSet = savedInstanceState.getBoolean("isNameSet");
            this.nameView.setText(name);
            this.phoneView.setText(phoneNumber);
            balanceView.setText(String.valueOf(balance));

        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        this.isNameSet =sharedPreferences.getBoolean("isNameSet",false);

        if(this.isNameSet){
            name = sharedPreferences.getString("username", "DefaultUser");
            phoneNumber = sharedPreferences.getString("phoneNumber", "123456789");
            balance = sharedPreferences.getInt("balance",0);
            this.nameView.setText(name);
            this.phoneView.setText(phoneNumber);
            balanceView.setText(String.valueOf(balance));

            //Toast.makeText(this, "Data loaded\nisNameSet: " + this.isNameSet, Toast.LENGTH_LONG).show();
        } else {
            setUser();
        }


        this.br = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(this.br,filter);
    }

    public void getLatestShopping(View view){
        Cursor res = db.getLastestTransaction("Vásárlás kártyával");
        res.moveToNext();
        Log.d("LATEST SHOPPING", "" +res.getString(0) + "   "  + res.getInt(1) + "    " + res.getString(2));
    }

    public void getLatestCredit(View view){
        Cursor res = db.getLastestTransaction("Jóvárírás");
        res.moveToNext();
        Log.d("LATEST CREDIT", "" +res.getString(0) + "   "  + res.getInt(1) + "    " + res.getString(2));
    }

    public void getLatestCashPickup(View view){
        Cursor res = db.getLastestTransaction("Készpénzfelvétel");
        res.moveToNext();
        Log.d("LATEST CASH", "" +res.getString(0) + "   "  + res.getInt(1) + "    " + res.getString(2));
    }

    public static boolean insertData(int value, String type, String partner){
        Cursor res = db.getAllData();
        if(res.getCount()==0){
            Log.d("SELECT ALL ", "NO DATA IN DATABASE");
        } else {
            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                buffer.append("Date: " + res.getString(0) + "\n");
                buffer.append("Value: " + res.getInt(1) + "\n");
                buffer.append("Type: " + res.getString(2) + "\n");
                buffer.append("Partner: " + res.getString(3) + "\n\n");
            }
            Log.d("SELECT ALL ", buffer.toString());
        }


        return db.insertData(LocalDateTime.now().toString(),value,type,partner);

    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }
    public static String getName() {
        return name;
    }

    public static void setBalance(int bal){
        balance=bal;
        balanceView.setText(String.valueOf(balance));
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
        balanceView.setText(String.valueOf(0));
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
        editor.putInt("balance",balance);

        editor.apply();
        //Toast.makeText(this, "Data saved", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("username",this.nameView.getText().toString());
        outState.putString("phoneNumber",this.phoneView.getText().toString());
        outState.putBoolean("isNameSet",this.isNameSet);
        outState.putInt("balance",balance);
        saveData();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(this.br);

        super.onDestroy();
    }

}
