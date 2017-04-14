package com.lhy.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.lhy.settings.fragment.BluetoothFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void bluetoothClick(View view) {
      Intent intent= new Intent(this, CommonActivity.class);
        intent.putExtra("flag", BluetoothFragment.FRAGMENT_BLUETOOTH);
        startActivity(intent);
    }
}
