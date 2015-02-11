package org.phone_lab.jouler.joulerbase.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.phone_lab.jouler.joulerbase.R;
import org.phone_lab.jouler.joulerbase.Utils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private boolean doubleBackToExitPressedOnce = false;
    protected static String highlightPackageName;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getClientApps();
        highlightPackageName = null;
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
            View rootView = inflater.inflate(R.layout.buttons_main, container, false);
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
        String key = getString(R.string.start_by);
        if (bundle.containsKey(key)) {
            highlightPackageName = bundle.getString(key);
            Log.d(Utils.TAG, "startByAnotherAPP " + key + " : " + highlightPackageName);
        }
    }

    public void click(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.save_exit:
                finish();
                break;
            case R.id.enter_selected:
                ClientListFragment clientListFragment = (ClientListFragment) getFragmentManager().findFragmentById(R.id.client_list);
                if (clientListFragment.mBound) {
                    clientListFragment.mService.enterSelected();
                } else {
                    Log.d(Utils.TAG, "Open failed, because service not bounded!");
                }
                finish();
                break;
            default:
                break;
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