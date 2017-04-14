package com.lhy.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.lhy.settings.fragment.BluetoothFragment;

/**
 * Created by lhy on 2017/4/14
 */
public class CommonActivity extends FragmentActivity {
    FrameLayout fl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        fl_content = (FrameLayout) findViewById(R.id.fl_content);
        Fragment fragment = null;
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();

        int flag = this.getIntent().getIntExtra("flag", 0);
        switch (flag) {
            case BluetoothFragment.FRAGMENT_BLUETOOTH:
                fragment = BluetoothFragment.getInstance();
                break;
        }
        transaction.replace(R.id.fl_content, fragment);
        transaction.commit();
    }
}
