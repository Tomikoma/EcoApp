package com.example.ecoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.Normalizer;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    String msg, phoneNo = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        /*StringBuilder stb = new StringBuilder();
        stb.append("Action: " + intent.getAction());
        String log = stb.toString();
        Toast.makeText(context, log, Toast.LENGTH_SHORT).show();*/
        Log.i(TAG, "Intent Received: " + intent.getAction());

        if(intent.getAction() == SMS_RECEIVED) {
            Bundle dataBundle = intent.getExtras();
            if(dataBundle!= null) {
                Object[] mypdu = (Object[])dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[mypdu.length];

                for (int i =0; i<mypdu.length; i++){

                    if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
                        String format = dataBundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i], format);
                    } else {
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg = message[i].getMessageBody();
                    phoneNo = message[i].getOriginatingAddress();
                }
                Toast.makeText(context,"Message: " + msg + "\nNumber: " + phoneNo, Toast.LENGTH_LONG ).show();
                if(phoneNo.equals(MainActivity.getPhoneNumber()) && MainActivity.getPhoneNumber()!= null) {
                    //Toast.makeText(context,"Érkezett a banktól SMS!\n", Toast.LENGTH_LONG).show();
                    String convertedMsg = Normalizer.normalize(msg,Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();

                    /*Toast.makeText(context,"Érkezett a banktól SMS!\nÖsszeg: " + getForintValueOfString(convertedMsg)
                            + "\nTípus: " + getTypeValueOfString(convertedMsg)
                            + "\nPartner: " + getPartnerValueOfString(convertedMsg), Toast.LENGTH_LONG).show();*/
                    boolean isDataInserted = MainActivity.insertData(getForintValueOfString(convertedMsg),getTypeValueOfString(convertedMsg),getPartnerValueOfString(convertedMsg));
                    //MainActivity.setBalance(getBalanceFromString(convertedMsg));
                    Toast.makeText(context, (isDataInserted? "SMS adatai elmentve!" : "SMS adatait nem sikerült elmenteni") + "\nEgyenlege: " + getBalanceFromString(convertedMsg), Toast.LENGTH_LONG).show(); //ird majd at (ne string literalok legyen itt)

                }
            }
        }


    }

    private int getForintValueOfString(String convertedMsg) {
        if(convertedMsg.contains("vasarlas") || convertedMsg.contains("kp.felvet")){
            String valueString = convertedMsg.substring(convertedMsg.indexOf("osszeg: ")+8,convertedMsg.indexOf(".-ft"));
            String[] splittedString = valueString.split(" ");
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
            return "Jóvárírás";
        }
        return null;
    }

    private String getPartnerValueOfString(String convertedMsg) {
        if(convertedMsg.contains("vasarlas") || convertedMsg.contains("kp.felvet")){
            return convertedMsg.substring(convertedMsg.indexOf(".-ft")+5, convertedMsg.indexOf("egyenleg"));
        } else if(convertedMsg.contains("jovairas")){
            String name = Normalizer.normalize(MainActivity.getName(),Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
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




}
