package org.phone_lab.jouler.joulerbase.activities;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.phone_lab.jouler.joulerbase.R;
import org.phone_lab.jouler.joulerbase.Utils;
import org.phone_lab.jouler.joulerbase.services.JoulerBaseService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xcv58 on 1/21/15.
 */
public class ClientListFragment extends ListFragment {
    private ClientAdapter clientAdapter;
    private List<Client> clientList;
    private ListView listView;

    private boolean mBound = false;
    private JoulerBaseService mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(Utils.TAG, "bind service from fragment");

            JoulerBaseService.LocalBinder binder = (JoulerBaseService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.reset();

            linkService(clientList);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(Utils.TAG, "unbind service from fragment");
            mService = null;
            mBound = false;
        }
    };

    public ClientListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.client_list, container, false);
        clientList = getClientApps();

        clientAdapter = new ClientAdapter(getActivity(), clientList);
        setListAdapter(clientAdapter);

        listView = (ListView) rootView.findViewWithTag(getString(R.string.list_view_tag));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(), JoulerBaseService.class);
        getActivity().bindService(intent, mConnection, getActivity().BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        // save option for persistence.
        Log.d(Utils.TAG, "onPause Run");
        if (mBound) {
            Log.d(Utils.TAG, "mBound is true");
            mService.flush();
            getActivity().unbindService(mConnection);
        }
        super.onPause();
    }

    @Override
    public void onListItemClick(android.widget.ListView l, android.view.View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Client client = clientList.get(position);
        if (client.isSelected()) {
            mService.setChoosed(client);
        } else {
            mService.setSelected(client);
        }
        clientAdapter.notifyDataSetChanged();
    }

    public List<Client> getClientApps() {
        List<Client> result = new ArrayList<Client>();
        PackageManager packageManager = getActivity().getPackageManager();
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            if (PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(getResources().getString(R.string.permission_name), packageInfo.packageName)) {
                result.add(new Client(packageInfo, packageManager));
            }
        }
        return result;
    }

    private void linkService(List<Client> list) {
        for (Client client : list) {
            client.setmService(mService);
        }
        clientAdapter.notifyDataSetChanged();
    }
}
