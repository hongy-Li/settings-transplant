package com.lhy.settings.bluetooth;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.lhy.settings.R;


/**
 * Created by chirenjie on 2016/11/24.
 */
public class BluetoothEditePreference extends Preference {
    public BluetoothEditePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.edit_preference_layout);
    }

    public BluetoothEditePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.edit_preference_layout);
    }

    public BluetoothEditePreference(Context context) {
        super(context);
        setLayoutResource(R.layout.edit_preference_layout);
    }
}
