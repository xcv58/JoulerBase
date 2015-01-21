package org.phone_lab.jouler.joulerbase.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.phone_lab.jouler.joulerbase.services.JoulerBaseService;

/**
 * Created by xcv58 on 1/18/15.
 */
public class StartupReceiver extends BroadcastReceiver {
    public final static String TAG = "JoulerBaseStartupReceiver";
    public final static String START_MODE = "Start mode";
    public final static String BOOT = "Boot Completeed";
    public final static String SCHEDULED = "Scheduled";
    public final static String ACTIVITY = "From Activity";


    private final static long INTERVAL = 1000L * 60L * 60L;
    //    private final static long INTERVAL = 1000L * 60L;
    private AlarmManager alarmMgr;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            if (context != null) {
                Intent serviceIntent = new Intent(context, JoulerBaseService.class);
                serviceIntent.putExtra(START_MODE, BOOT);
                context.startService(serviceIntent);
                Log.d(TAG, "Daemon start");
                setRepeatAlarm(context, serviceIntent);
            }
        }
    }

    private void setRepeatAlarm(Context context, Intent serviceIntent) {
        serviceIntent.putExtra(START_MODE, SCHEDULED);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(alarmMgr.RTC, System.currentTimeMillis() + INTERVAL, INTERVAL, pendingIntent);
    }
}