package org.phone_lab.jouler.joulerbase.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.phone_lab.jouler.joulerbase.R;
import org.phone_lab.jouler.joulerbase.Utils;
import org.phone_lab.jouler.joulerbase.services.JoulerBaseService;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private boolean mBound = false;
    private JoulerBaseService mService;

    private boolean doubleBackToExitPressedOnce = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            JoulerBaseService.LocalBinder binder = (JoulerBaseService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, JoulerBaseService.class);
        startService(intent);
        bindService(intent, mConnection, this.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getClientApps();

        startByAnotherApp(getIntent());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.description_main, container, false);
            return rootView;
        }

        @Override
        public void onPause() {
            // save option for persistence.
            super.onPause();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startByAnotherApp(intent);
    }

    private void startByAnotherApp(Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(Utils.TAG, "startByAnotherAPP" + intent.toString());
        if (bundle == null) {
            return;
        }
        for (String key : bundle.keySet()) {
            Log.d(Utils.TAG, "startByAnotherAPP " + key + " : " + bundle.get(key));
        }
    }

    public void click(View view) {
        if (mBound) {
            Log.d(Utils.TAG, "Bounded");
            String packageName = mService.getChoosedPackageName();
            Log.d(Utils.TAG, "PACKAGE NAME " + packageName);
        } else {
            Log.d(Utils.TAG, "not Bound");
        }
    }

    private void openSettings() {
        Intent intent = new Intent(getBaseContext(), SettingActivity.class);
        startActivity(intent);
    }

    public List<PackageInfo> getClientApps() {
        List<PackageInfo> results = new ArrayList<PackageInfo>();
        PackageManager packageManager = getBaseContext().getPackageManager();
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { continue; }
            if (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(getResources().getString(R.string.permission_name), packageInfo.packageName)) {
                results.add(packageInfo);
            }
        }
        Log.d(Utils.TAG, "Apps : " + results.toString());
        return results;
    }
}