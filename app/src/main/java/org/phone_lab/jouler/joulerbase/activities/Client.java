package org.phone_lab.jouler.joulerbase.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by xcv58 on 1/21/15.
 */
public class Client {
    private PackageInfo packageInfo;

    public Client(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public String getAppName(PackageManager pm) {
        return packageInfo.applicationInfo.loadLabel(pm).toString();
    }

    public String getDescription(PackageManager pm) {
        CharSequence cs = packageInfo.applicationInfo.loadDescription(pm);
        return cs == null ? "No description" : cs.toString();
    }

    public Drawable getIcon(PackageManager pm) {
        return packageInfo.applicationInfo.loadIcon(pm);
    }
}
