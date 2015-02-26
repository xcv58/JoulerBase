package org.phone_lab.jouler.joulerbase.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.JoulerPolicy;
import android.os.JoulerStats;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.phone_lab.jouler.joulerbase.Utils;

import java.net.ContentHandler;

/**
 * Created by xcv58 on 1/21/15.
 */
public class JoulerFunction {
    private JoulerPolicy joulerPolicy;
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final String UID = "UID";
    private static final String PREVIOUS_BRIGHTNESS = "PREVIOUS_BRIGHTNESS ";
    private static final String PREVIOUS_BRIGHTNESS_MODE = "PREVIOUS_BRIGHTNESS_MODE ";
    private static final String PREFERENCE = "Jouler_Base_Preference";

    private Context context;
    private static int previousBrightness;
    private static int previousBrightnessMode;

    public JoulerFunction(Context context) {
        this.context = context;
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

            json.put("BluetoothEnergy", joulerStats.mSystemStats.getBluetoothEnergy());
            json.put("CurrentBgDischargeRate", joulerStats.mSystemStats.getCurrentBgDischargeRate());
            json.put("CurrentDischargeRate", joulerStats.mSystemStats.getCurrentDischargeRate());
            json.put("CurrentFgDischargeRate", joulerStats.mSystemStats.getCurrentFgDischargeRate());
            json.put("IdleEnergy", joulerStats.mSystemStats.getIdleEnergy());
            json.put("PhoneEnergy", joulerStats.mSystemStats.getPhoneEnergy());
            json.put("RadioEnergy", joulerStats.mSystemStats.getRadioEnergy());
            json.put("ScreenEnergy", joulerStats.mSystemStats.getScreenEnergy());
            json.put("TotalScreenOffTime", joulerStats.mSystemStats.getTotalScreenOffTime());
            json.put("TotalScreenOnTime", joulerStats.mSystemStats.getTotalScreenOnTime());
            json.put("Uptime", joulerStats.mSystemStats.getUptime());
            json.put("WifiEnergy", joulerStats.mSystemStats.getWifiEnergy());

            JSONObject packageDetails = new JSONObject();

            for (int i = 0; i < joulerStats.mUidArray.size(); i++) {
                JoulerStats.UidStats u = joulerStats.mUidArray.valueAt(i);
                if (u.packageName == null) {
                    continue;
                }
                packageDetails.put(u.packageName, getJSON(u));
            }

            json.put("packageDetails", packageDetails);
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
            json.put("packageName", u.packageName);
            json.put("uiActivity", u.uiActivity);
            json.put("Uid", u.getUid());
            json.put("FgEnergy", u.getFgEnergy());
            json.put("BgEnergy", u.getBgEnergy());
            json.put("CpuEnergy", u.getCpuEnergy());
            json.put("WakelockEnergy", u.getWakelockEnergy());
            json.put("WifiEnergy", u.getWifiEnergy());
            json.put("SensorEnergy", u.getSensorEnergy());
            json.put("MobileDataEnergy", u.getMobileDataEnergy());
            json.put("WifiDataEnergy", u.getWifiDataEnergy());
            json.put("VideoEnergy", u.getVideoEnergy());
            json.put("AudioEnergy", u.getAudioEnergy());
            json.put("Frame", u.getFrame());
            json.put("State", u.getState());
            json.put("Throttle", u.getThrottle());
            json.put("Count", u.getCount());
            json.put("UsageTime", u.getUsageTime());
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

    public void addRateLimitRule(int uid) {
        Log.d(Utils.TAG, "run addRateLimitRule");
        try {
            joulerPolicy.addRateLimitRule(uid);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void delRateLimitRule(int uid) {
        try {
            Log.d(Utils.TAG, "run delRateLimitRule");
            joulerPolicy.delRateLimitRule(uid);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public int getPriority(int uid) {
        return joulerPolicy.getPriority(uid);
    }

    public void resetPriority(int uid, int priority) {
        joulerPolicy.resetPriority(uid, priority);
    }

    private void setPreviousBrightness(int previousBrightness, int previousBrightnessMode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE, 0).edit();
        editor.putInt(PREVIOUS_BRIGHTNESS, previousBrightness);
        editor.putInt(PREVIOUS_BRIGHTNESS_MODE, previousBrightnessMode);
        editor.commit();
    }

    private void getPreviousBrightness() {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        previousBrightness = settings.getInt(PREVIOUS_BRIGHTNESS, previousBrightness);
        previousBrightnessMode = settings.getInt(PREVIOUS_BRIGHTNESS_MODE, previousBrightnessMode);
    }

    public void lowBrightness() {
        try {
            previousBrightness = android.provider.Settings.System.getInt(
                    context.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
            previousBrightnessMode = android.provider.Settings.System.getInt(
                    context.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE);
            Log.d(Utils.TAG, "Previous brightness is: " + previousBrightness + ". Mode is: " + previousBrightnessMode);
            setPreviousBrightness(previousBrightness, previousBrightnessMode);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        android.provider.Settings.System.putInt(context.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                getLowBrightness());
        android.provider.Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public void restBrightness() {
        getPreviousBrightness();
        android.provider.Settings.System.putInt(context.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                previousBrightness);
        android.provider.Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                previousBrightnessMode);
    }

    public int getLowBrightness() {
        return previousBrightness / Math.max((int)Math.log(previousBrightness) - 2, 1);
    }
}
