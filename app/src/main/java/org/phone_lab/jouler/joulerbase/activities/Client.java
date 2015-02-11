package org.phone_lab.jouler.joulerbase.activities;

import android.app.FragmentBreadCrumbs;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

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
    private ClientListFragment clientListFragment;
    private final static String NO_DESCRIPTION = "no description";
    ClientClickListener clientClickListener;

    public class ClientClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            clientListFragment.mService.setChoosed(Client.this);
            clientListFragment.clientAdapter.notifyDataSetChanged();
        }
    }

    public Client(PackageInfo packageInfo, PackageManager packageManager, ClientListFragment clientListFragment) {
        this.packageInfo = packageInfo;
        appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

        CharSequence charSequence = packageInfo.applicationInfo.loadDescription(packageManager);
        description = charSequence == null ? NO_DESCRIPTION : charSequence.toString();

        icon = packageInfo.applicationInfo.loadIcon(packageManager);

        clientClickListener = new ClientClickListener();

        this.clientListFragment = clientListFragment;
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
        if (clientListFragment.mService == null) {
            Log.d(Utils.TAG, "No Service set in Client");
            return false;
        }
        return clientListFragment.mService.isSelected(this);
    }

    public boolean isHighlight() {
        if (clientListFragment.mService == null) {
            Log.d(Utils.TAG, "No Service set in Client");
            return false;
        }
        return clientListFragment.mService.isHighlight(this);
    }

    public boolean isChoosed() {
        if (clientListFragment.mService == null) {
            Log.d(Utils.TAG, "No Service set in Client");
            return false;
        }
        return clientListFragment.mService.isChoosed(this);
    }

    public String getPackageName() {
        return packageInfo.packageName;
    }

    public int getUid() {
        return packageInfo.applicationInfo.uid;
    }
}
