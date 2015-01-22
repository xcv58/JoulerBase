package org.phone_lab.jouler.joulerbase.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.phone_lab.jouler.joulerbase.Utils;
import org.phone_lab.jouler.joulerbase.services.JoulerBaseService;

/**
 * Created by xcv58 on 1/21/15.
 */
public class Client {
    private PackageInfo packageInfo;
    private String appName;
    private String description;
    private Drawable icon;
    private JoulerBaseService mService;
    private final static String NO_DESCRIPTION = "no description";

    public Client(PackageInfo packageInfo, PackageManager packageManager) {
        this.packageInfo = packageInfo;
        appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

        CharSequence charSequence = packageInfo.applicationInfo.loadDescription(packageManager);
        description = charSequence == null ? NO_DESCRIPTION : charSequence.toString();

        icon = packageInfo.applicationInfo.loadIcon(packageManager);
    }

    public String getAppName() {
        return appName;
    }

    public String getDescription() {
        return description;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isSelected() {
        if (mService == null) {
            Log.d(Utils.TAG, "No Service set in Client");
            return false;
        }
        return mService.isSelected(this);
    }

    public boolean isChoosed() {
        if (mService == null) {
            Log.d(Utils.TAG, "No Service set in Client");
            return false;
        }
        return mService.isChoosed(this);
    }

    public void setService(JoulerBaseService service) {
        this.mService = service;
    }
}
