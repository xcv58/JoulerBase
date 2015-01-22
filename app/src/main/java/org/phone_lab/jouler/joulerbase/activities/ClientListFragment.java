package org.phone_lab.jouler.joulerbase.activities;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.phone_lab.jouler.joulerbase.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xcv58 on 1/21/15.
 */
public class ClientListFragment extends ListFragment {
    private ClientAdapter clientAdapter;

    public ClientListFragment() {
        Log.d("SIZE", "123");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.client_list, container, false);

        clientAdapter = new ClientAdapter(getActivity(), getClientApps());
        setListAdapter(clientAdapter);
        clientAdapter.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void onPause() {
        // save option for persistence.
        super.onPause();
    }

    public List<Client> getClientApps() {
        List<Client> results = new ArrayList<Client>();
        PackageManager packageManager = getActivity().getPackageManager();
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { continue; }
            if (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(getResources().getString(R.string.permission_name), packageInfo.packageName)) {
                results.add(new Client(packageInfo));
            }
        }
        return results;
    }
}
