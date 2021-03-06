package com.example.fengling.vitontest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmService extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmManager alarmMgr1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            AlarmManager alarmMgr2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Class myService = null;

            try {
                myService = Class.forName("com.example.fengling.vitontest.DataTransferService");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Intent myIntent1 = new Intent(context, myService);
            Intent myIntent2 = new Intent(context, myService);
            myIntent2.setAction("TERMINATION");

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            calendar1.set(Calendar.HOUR_OF_DAY, 9);


            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTimeInMillis(System.currentTimeMillis());
            calendar2.set(Calendar.HOUR_OF_DAY, 17);

            PendingIntent alarmIntent1 = PendingIntent.getService(context, 0, myIntent1, 0);
            PendingIntent alarmIntent2 = PendingIntent.getService(context, 0, myIntent2, 0);

// With setInexactRepeating(), you have to use one of the AlarmManager interval
// constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr1.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent1);
            //alarmMgr2.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            // SystemClock.elapsedRealtime() +
            // 20 * 1000, alarmIntent2);


            alarmMgr2.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent2);

            //if booted between 9 to 5, start service immediately
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hour > 8 && hour < 17) {
                Toast.makeText(context, "between 9 to 17!", Toast.LENGTH_LONG).show();
                context.startService(myIntent1);

            }

        }
    }
}
