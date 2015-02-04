package org.phone_lab.jouler.joulerbase.services;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;
import org.phone_lab.jouler.joulerbase.IJoulerBaseService;
import org.phone_lab.jouler.joulerbase.R;
import org.phone_lab.jouler.joulerbase.Utils;
import org.phone_lab.jouler.joulerbase.activities.Client;
import org.phone_lab.jouler.joulerbase.receivers.StartupReceiver;

import java.util.*;

/**
 * Created by xcv58 on 1/18/15.
 */
public class JoulerBaseService extends Service {
    public static final String TAG = "JoulerBaseService";
    private JoulerFunction joulerFunction;
    private LocalBinder localBinder = new LocalBinder();
    private Client selectedClient;
    private Client choosedClient;

    IJoulerBaseService.Stub mBinder = new IJoulerBaseService.Stub() {
        @Override
        public boolean checkPermission() {
            return checkCallingChoosed(getCallingUid());
        }

        @Override
        public void test(String title, String text) {
            if (!checkCallingChoosed(getCallingUid())) {
                return;
            }
            Log.d(TAG, "CallingUID: " + getCallingUid() + ", CallingPid: " + getCallingPid());
            makeNotification(title, text, 001);
        }

        @Override
        public String getStatistics() {
            if (!checkCallingChoosed(getCallingUid())) {
                return "";
            }
            return joulerFunction.getStatistics();
        }

        @Override
        public void controlCpuMaxFrequency(int freq) {
            if (!checkCallingChoosed(getCallingUid())) {
                return;
            }
            joulerFunction.controlCpuMaxFrequency(freq);
        }

        @Override
        public int[] getAllCpuFrequencies() {
            if (!checkCallingChoosed(getCallingUid())) {
                return null;
            }
            return joulerFunction.getAllCpuFrequencies();
        }

        @Override
        public void addRateLimitRule(int uid) {
            if (!checkCallingChoosed(getCallingUid())) {
                return;
            }
            joulerFunction.addRateLimitRule(uid);
        }

        @Override
        public void delRateLimitRule(int uid) {
            if (!checkCallingChoosed(getCallingUid())) {
                return;
            }
            joulerFunction.delRateLimitRule(uid);
        }

        @Override
        public int getPriority(int uid) {
            if (!checkCallingChoosed(getCallingUid())) {
                return -1;
            }
            return joulerFunction.getPriority(uid);
        }

        @Override
        public void resetPriority(int uid, int priority) {
            if (!checkCallingChoosed(getCallingUid())) {
                return;
            }
            joulerFunction.resetPriority(uid, priority);
        }
    };

    public class LocalBinder extends Binder {
        public JoulerBaseService getService() {
            return JoulerBaseService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("JOULERINTENT", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (StartupReceiver.BOOT.equals(bundle.get(StartupReceiver.START_MODE))) {
                Log.d(Utils.TAG, "Start by boot completed");
                // wakeup selected app.
                wakeUp(getChoosedPackageName());
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() executed.");
        if (intent != null ) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(IJoulerBaseService.class.getName())) {
                    Log.d(TAG, "Bind via AIDL.");
                    return mBinder;
                }
            }
        }
        Log.d(TAG, "Bind from inner process.");
        return localBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy executed.");
    }

    public void makeNotification(String title, String text, int id) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(text);

        NotificationCompat.Builder mBuilder = new NotificationCompat.
                Builder(getBaseContext())
                .setSmallIcon(R.drawable.ic_launcher)
                .setStyle(bigTextStyle);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(id, mBuilder.build());
        return;
    }

    private void initJoulerFunction() {
        if (joulerFunction == null) {
            joulerFunction = new JoulerFunction(getBaseContext());
        }
    }

    public String getChoosedPackageName() {
        initJoulerFunction();
        return joulerFunction.getChoosedPackageName(getBaseContext());
    }

    public int getChoosedUid() {
        initJoulerFunction();
        return joulerFunction.getChoosedUid(getBaseContext());
    }

    public boolean setChoosedPackageName(String packageName) {
        initJoulerFunction();
        return joulerFunction.setChoosedPackageName(getBaseContext(), packageName);
    }

    public boolean setChoosedUid(int uid) {
        initJoulerFunction();
        return joulerFunction.setChoosedUid(getBaseContext(), uid);
    }

    public boolean checkCallingChoosed(int uid) {
        initJoulerFunction();
        return joulerFunction.isChoosed(getBaseContext(), uid);
    }

    public void setChoosed(Client client) {
        choosedClient = client;
    }

    public boolean isChoosed(Client client) {
        if (choosedClient != null) {
            return choosedClient == client;
        }

        if (this.getChoosedPackageName().equals(client.getPackageName())) {
            this.choosedClient = client;
            return true;
        }

        return false;
    }

    public void setSelected(Client client) {
        this.selectedClient = client;
    }

    public boolean isSelected(Client client) {
        return this.selectedClient == client;
    }

    public void reset() {
        this.choosedClient = null;
        this.selectedClient = null;
    }

    public void flush() {
        Log.d(Utils.TAG, "Flush start");
        if (choosedClient == null) {
            Log.d(Utils.TAG, "Flush end by null choosedClient");
            return;
        }
        Log.d(Utils.TAG, "Flush actually run");
        String originalPackageName = getChoosedPackageName();
        String newPackageName = choosedClient.getPackageName();
        if (!originalPackageName.equals(newPackageName)) {
            // put old app sleep
            sleep(originalPackageName);
            // wake up new app
            wakeUp(newPackageName);
            this.setChoosedPackageName(choosedClient.getPackageName());
        }
        this.setChoosedUid(choosedClient.getUid());
        return;
    }

    private void sleep(String packageName) {
        if (packageName == null || packageName.isEmpty() || packageName.equals("")) {
            Log.d(Utils.TAG, "Sleep cancel because packageName is invalid");
            return;
        }
        String action = packageName + getString(R.string.stop_suffix);
        Log.d(Utils.TAG, "Sleep " + action);
        Intent wakeUpIntent = new Intent(action);
        sendBroadcast(wakeUpIntent);
    }

    private void wakeUp(String packageName) {
        if (packageName == null || packageName.isEmpty() || packageName.equals("")) {
            Log.d(Utils.TAG, "Wake up cancel because packageName is invalid");
            return;
        }
        String action = packageName + getString(R.string.start_suffix);
        Log.d(Utils.TAG, "Wake up " + action);
        Intent wakeUpIntent = new Intent(action);
        sendBroadcast(wakeUpIntent);
    }
}
