package org.phone_lab.jouler.joulerbase.activities;

import android.app.Activity;
import android.os.Bundle;

import org.phone_lab.jouler.joulerbase.R;


/**
 * Created by xcv58 on 1/20/15.
 */
public class SettingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.setting);
    }
}
