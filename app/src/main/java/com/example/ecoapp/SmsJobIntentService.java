package com.example.ecoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

public class SmsJobIntentService extends JobIntentService {

    private static final int JOB_ID = 1000;
    private BroadcastReceiver br;

    static void enqueueWork(Context context, Intent work){
        enqueueWork(context,SmsJobIntentService.class,JOB_ID,work);
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        br = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(br,filter);

        AlarmReceiver.setAlarm(false);
        stopSelf();
    }

    public void onDestroy(){
        unregisterReceiver(br);
        super.onDestroy();
    }

    @Override
    public boolean onStopCurrentWork(){
        return super.onStopCurrentWork();
    }
}
