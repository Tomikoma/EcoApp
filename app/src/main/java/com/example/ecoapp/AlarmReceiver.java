package com.example.ecoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";

    private static Context ctx = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        /* enqueue the job */
        SmsJobIntentService.enqueueWork(context, intent);
    }
    public static void cancelAlarm() {
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

        /* cancel any pending alarm */
        alarm.cancel(getPendingIntent());
    }
    public static void setAlarm(boolean force) {
        cancelAlarm();
        AlarmManager alarm = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        // EVERY X MINUTES
        long delay = (1000 * 60 * 1);
        long when = System.currentTimeMillis();
        if (!force) {
            when += delay;
        }

        /* fire the broadcast */
        alarm.set(AlarmManager.RTC_WAKEUP, when, getPendingIntent());
    }
    private static PendingIntent getPendingIntent() {
        Context ctxx = ctx;   /* get the application context */
        Intent alarmIntent = new Intent(ctxx, AlarmReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);

        return PendingIntent.getBroadcast(ctxx, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
