package org.phone_lab.jouler.joulerbase.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.JoulerPolicy;
import android.os.JoulerStats;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ContentHandler;

/**
 * Created by xcv58 on 1/21/15.
 */
public class JoulerFunction {
    private JoulerPolicy joulerPolicy;
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final String UID = "UID";
    private static final String PREFERENCE = "Jouler_Base_Preference";

    public JoulerFunction(Context context) {
        if (joulerPolicy == null) {
            try {
                joulerPolicy = (JoulerPolicy) context.getSystemService(context.JOULER_SERVICE);
            } catch (NoClassDefFoundError error) {
                error.printStackTrace();
            }
        }
    }

    public boolean isChoosed(Context context, String packageName) {
        return this.getChoosedPackageName(context).equals(packageName);
    }

    public boolean isChoosed(Context context, int uid) {
        return this.getChoosedUid(context) == uid;
    }

    public String getChoosedPackageName(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return settings.getString(PACKAGE_NAME, "");
    }

    public int getChoosedUid(Context context) {
        Log.d(JoulerBaseService.TAG, "get choosed uid");
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        return settings.getInt(UID, -1);
    }

    public boolean setChoosedPackageName(Context context, String packageName) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE, 0).edit();
        editor.putString(PACKAGE_NAME, packageName);
        return editor.commit();
    }

    public boolean setChoosedUid(Context context, int uid) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE, 0).edit();
        editor.putInt(UID, uid);
        return editor.commit();
    }

    public String getStatistics() {
        return this.getJsonDetail().toString();
    }

    private JSONObject getJsonDetail() {
        JSONObject json = new JSONObject();
        try {
            byte[] bytes = joulerPolicy.getStatistics();
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(bytes, 0, bytes.length);
            parcel.setDataPosition(0); // this is extremely important!
            JoulerStats joulerStats = new JoulerStats(parcel);

            for (int i = 0; i < joulerStats.mUidArray.size(); i++) {
                JoulerStats.UidStats u = joulerStats.mUidArray.valueAt(i);
                if (u.packageName == null) {
                    continue;
                }
                json.put(u.packageName, getJSON(u));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private JSONObject getJSON(JoulerStats.UidStats u) {
        JSONObject json = new JSONObject();
        try {
            json.put("packagename", u.packageName);
            json.put("FgEnergy", u.getFgEnergy());
            json.put("BgEnergy", u.getBgEnergy());
            json.put("CPU", u.getCpuEnergy());
            json.put("Wakelock", u.getWakelockEnergy());
            json.put("Wifi", u.getWifiEnergy());
            json.put("Mobile Data", u.getMobileDataEnergy());
            json.put("Wifi Data", u.getWifiDataEnergy());
            json.put("Video", u.getVideoEnergy());
            json.put("Video", u.getVideoEnergy());
            json.put("Frames", u.getFrame());
            json.put("Launches", u.getCount());
            json.put("Usage time", u.getUsageTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void controlCpuMaxFrequency(int freq) {
        joulerPolicy.controlCpuMaxFrequency(freq);
        return;
    }

    public int[] getAllCpuFrequencies() {
        return joulerPolicy.getAllCpuFrequencies();
    }

    public void rateLimitForUid(int uid) {
        joulerPolicy.rateLimitForUid(uid);
    }

    public int getPriority(int uid) {
        return joulerPolicy.getPriority(uid);
    }

    public void resetPriority(int uid, int priority) {
        joulerPolicy.resetPriority(uid, priority);
    }
}
