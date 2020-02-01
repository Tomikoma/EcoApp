package com.example.ecoapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SetUsernameFragment.EditNameDialogListener{

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private BroadcastReceiver br;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private static final String SHARED_PREFS = "SsharedPrefs";
    public static final String CREDIT = "Jóváírás";
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


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        nameView.setText("DefaultUsername");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)){

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }

        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);


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

    /*
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String body ="EMPTY";
            do {
                String msgData = "";

                for(int idx=0;idx<cursor.getColumnCount();idx++)
                {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                    if(cursor.getColumnName(idx).equals("body"))
                        body += cursor.getString(idx);
                }
                Log.e("MSG",msgData);
                Toast.makeText(this,body , Toast.LENGTH_LONG).show();
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }*/




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu,menu);
        return true;
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
            res = db.getLastestTransaction("Jóváírás");
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
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch (item.getItemId()){
            case R.id.process_messages:
                Toast.makeText(this, "Üzenetek feldolgozása...", Toast.LENGTH_LONG).show();


                db.deleteAllTransactions();
                Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
                if (cursor.moveToFirst()) { // must check the result to prevent exception

                    do {
                        String msgData = "";
                        String tempNumber="";
                        String messageBody = null;
                        String date = null;
                        for(int i=0;i<cursor.getColumnCount();i++)
                        {
                            msgData += " " + cursor.getColumnName(i) + ":" + cursor.getString(i);
                            if(cursor.getColumnName(i).equals("address"))
                                tempNumber = cursor.getString(i);
                            if(cursor.getColumnName(i).equals("body"))
                                messageBody = cursor.getString(i);
                            if(cursor.getColumnName(i).equals("date_sent"))
                                date = new SimpleDateFormat("yyyy-MM-dd",new Locale("HU")).format(new Date(cursor.getLong(i)));
                        }
                        Log.e("MSG",msgData);
                        Log.e("MSG_DATE",date);
                        if(tempNumber.equals(phoneNumber) && messageBody!=null) {
                            Log.e("NUMBER:", phoneNumber);
                            String convertedMsg = Normalizer.normalize(messageBody, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
                            if(convertedMsg.contains("ervenytelenitett") || convertedMsg.contains("postai") || convertedMsg.contains("sikertelen")){
                                continue;
                            }
                            boolean isDataInserted = insertData(getForintValueOfString(convertedMsg), getTypeValueOfString(convertedMsg), getPartnerValueOfString(convertedMsg),date);
                        }
                    } while (cursor.moveToNext());
                } else {
                    // empty box, no SMS
                }

                break;
            case R.id.credits:
                intent = new Intent(this, PieChartActivity.class);
                intent.putExtra("PIE_CHART_TYPE",CREDIT);
                startActivity(intent);
                break;
            case R.id.shoppings:
                intent = new Intent(this, PieChartActivity.class);
                intent.putExtra("PIE_CHART_TYPE",SHOPPING);
                startActivity(intent);
                break;
            case R.id.pickups:
                intent = new Intent(this, PieChartActivity.class);
                intent.putExtra("PIE_CHART_TYPE",PICKUP);
                startActivity(intent);
                break;
        }
        return true;
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

    private int getForintValueOfString(String convertedMsg) {
        if(convertedMsg.contains("vasarlas") || convertedMsg.contains("kp.felvet")){
            String endString = ".-ft";
            if(convertedMsg.contains("huf")){
                endString = "huf";
            }
            String valueString = convertedMsg.substring(convertedMsg.indexOf("osszeg: ")+8,convertedMsg.indexOf(endString));
            String regexString = valueString.contains(",") ? "," : " ";
            String[] splittedString = valueString.split(regexString);
            String unspacedValue ="";
            for (String s : splittedString) {
                unspacedValue+= s;
            }
            int value = Integer.parseInt(unspacedValue);
            return value;
        } else if(convertedMsg.contains("jovairas")) {
            String valueString = convertedMsg.substring(convertedMsg.indexOf("jovairas")+9,convertedMsg.indexOf("ft"));
            String[] splittedString = valueString.split(",");
            String unspacedValue ="";
            for (String s : splittedString) {
                unspacedValue+= s;
            }
            int value = Integer.parseInt(unspacedValue);
            return value;
        }
        return -1;
    }

    private String getTypeValueOfString(String convertedMsg) {
        if(convertedMsg.contains("vasarlas")){
            return "Vásárlás kártyával";
        } else if(convertedMsg.contains("kp.felvet")) {
            return "Készpénzfelvétel";
        } else if(convertedMsg.contains("jovairas")){
            return "Jóváírás";
        }
        return null;
    }

    private String getPartnerValueOfString(String convertedMsg) {
        if(convertedMsg.contains("vasarlas") || convertedMsg.contains("kp.felvet")){
            if(convertedMsg.contains("huf")) {
                String[] splittedString = convertedMsg.split(",");
                return splittedString[splittedString.length-2];
                //return convertedMsg.substring(convertedMsg.indexOf("huf") + 5, convertedMsg.indexOf("egyenleg"));
            } else
                return convertedMsg.substring(convertedMsg.indexOf(".-ft")+5, convertedMsg.indexOf("egyenleg"));
        } else if(convertedMsg.contains("jovairas")){
            String name = Normalizer.normalize(this.name,Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
            if(convertedMsg.contains(name)) {
                return convertedMsg.substring(convertedMsg.lastIndexOf("ft") + 2, convertedMsg.indexOf(name));
            } else {
                return "Nem sikerült lekérni a jóváíráshoz tartozó partnert";
            }
        }
        return null;
    }

    private int getBalanceFromString(String convertedMsg){
        if(convertedMsg.contains("vasarlas") || convertedMsg.contains("kp.felvet")){
            String valueString = convertedMsg.substring(convertedMsg.lastIndexOf("egyenleg")+9,convertedMsg.lastIndexOf("ft"));
            return Integer.parseInt(valueString);

        } else if(convertedMsg.contains("jovairas")){
            String valueString = convertedMsg.substring(convertedMsg.lastIndexOf("egyenleg+")+9,convertedMsg.lastIndexOf("ft"));
            String[] splittedString = valueString.split(",");
            String unspacedValue ="";
            for (String s : splittedString) {
                unspacedValue+= s;
            }
            return Integer.parseInt(unspacedValue);

        }
        return -1;
    }

    private  boolean insertData(int value, String type, String partner, String date){
        return db.insertData(date,value,type,partner);

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
        super.onDestroy();
    }

}
