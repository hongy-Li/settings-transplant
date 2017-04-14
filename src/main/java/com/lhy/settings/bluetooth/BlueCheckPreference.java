package com.lhy.settings.bluetooth;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

import com.lhy.settings.R;


/**
 * Created by chirenjie on 2016/11/24.
 */
public class BlueCheckPreference extends CheckBoxPreference {
    public BlueCheckPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.blue_check_layout);
    }

    public BlueCheckPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.blue_check_layout);
    }

    public BlueCheckPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.blue_check_layout);

    }
}
