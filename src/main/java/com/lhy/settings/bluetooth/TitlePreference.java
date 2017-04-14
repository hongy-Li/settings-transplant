package com.lhy.settings.bluetooth;

import android.content.Context;
import android.preference.PreferenceGroup;
import android.util.AttributeSet;

import com.lhy.settings.R;


/**
 * Created by huanshuai on 2016/11/22.
 */
public class TitlePreference extends PreferenceGroup{
    public TitlePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_progress_category);
    }
    public TitlePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutResource(R.layout.preference_progress_category);
    }
}
