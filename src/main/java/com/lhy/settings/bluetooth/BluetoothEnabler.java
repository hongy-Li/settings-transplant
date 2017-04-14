/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lhy.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * BluetoothEnabler is a helper to manage the Bluetooth on/off checkbox
 * preference. It turns on/off Bluetooth and ensures the summary of the
 * preference reflects the current state.
 */
public final class BluetoothEnabler implements CompoundButton.OnCheckedChangeListener {
    private final Context mContext;
//    private Switch mSwitch;
    private boolean mValidListener;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final IntentFilter mIntentFilter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Broadcast receiver is always running on the UI thread here,
            // so we don't need consider thread synchronization.
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            handleStateChanged(state);
        }
    };

    public BluetoothEnabler(Context context, CheckBox checkBox_) {
        mContext = context;
        mCheckBox = checkBox_;
        mValidListener = false;
        //首先判断是否支持蓝牙  
        LocalBluetoothManager manager = LocalBluetoothManager.getInstance(context);
        if (manager == null) {
            // Bluetooth is not supported
            mLocalAdapter = null;
            mCheckBox.setEnabled(false);
        } else {
            mLocalAdapter = manager.getBluetoothAdapter();
        }
        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    }
    	//在BluetoothSettings 的OnResume()方法中调用
    public void resume() {
        if (mLocalAdapter == null) {
            mCheckBox.setEnabled(false);
            return;
        }

        // Bluetooth state is not sticky, so set it manually
        handleStateChanged(mLocalAdapter.getBluetoothState());

        mContext.registerReceiver(mReceiver, mIntentFilter);
        mCheckBox.setOnCheckedChangeListener(this);
        
        
        mValidListener = true;
    }

    public void pause() {
        if (mLocalAdapter == null) {
            return;
        }

        mContext.unregisterReceiver(mReceiver);
        mCheckBox.setOnCheckedChangeListener(null);
        mValidListener = false;
    }


    CheckBox mCheckBox;
    public void setCheckBox(CheckBox checkBox_){
        if (mCheckBox == checkBox_) return;
        mCheckBox.setOnCheckedChangeListener(null);
        mCheckBox = checkBox_;
        mCheckBox.setOnCheckedChangeListener(mValidListener ? this : null);

        int bluetoothState = BluetoothAdapter.STATE_OFF;
        if (mLocalAdapter != null) bluetoothState = mLocalAdapter.getBluetoothState();
        boolean isOn = bluetoothState == BluetoothAdapter.STATE_ON;
        boolean isOff = bluetoothState == BluetoothAdapter.STATE_OFF;
        setChecked(isOn);
        mCheckBox.setEnabled(isOn || isOff);

    }

    /**
     * 设置开关键
     * @param
     */
//    public void setSwitch(Switch switch_) {
//        if (mSwitch == switch_) return;
//        mSwitch.setOnCheckedChangeListener(null);
//        mSwitch = switch_;
//        mSwitch.setOnCheckedChangeListener(mValidListener ? this : null);
//
//        int bluetoothState = BluetoothAdapter.STATE_OFF;
//        if (mLocalAdapter != null) bluetoothState = mLocalAdapter.getBluetoothState();
//        boolean isOn = bluetoothState == BluetoothAdapter.STATE_ON;
//        boolean isOff = bluetoothState == BluetoothAdapter.STATE_OFF;
//        setChecked(isOn);
//        mSwitch.setEnabled(isOn || isOff);
//    }
    
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Show toast message if Bluetooth is not allowed in airplane mode
//        if (isChecked && !WirelessSettings.isRadioAllowed(mContext, Settings.Global.RADIO_BLUETOOTH)) {
////            Toast.makeText(mContext, R.string.wifi_in_airplane_mode, Toast.LENGTH_SHORT).show();
//            Toast.makeText(mContext, "飞行模式", Toast.LENGTH_SHORT).show();
//            // Reset switch to off
//            buttonView.setChecked(false);
//        }

        if (mLocalAdapter != null) {
        	//switch开关状态改变，对系统本地蓝牙状态进行设置  
            mLocalAdapter.setBluetoothEnabled(isChecked);
        }
      //当switch状态进行改变时，让其不可点击,防止快速连续点击
        mCheckBox.setEnabled(false);
    }

    void handleStateChanged(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_TURNING_ON:
                mCheckBox.setEnabled(false);
                break;
            case BluetoothAdapter.STATE_ON:
                setChecked(true);
                mCheckBox.setEnabled(true);
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                mCheckBox.setEnabled(false);
                break;
            case BluetoothAdapter.STATE_OFF:
                setChecked(false);
                mCheckBox.setEnabled(true);
                break;
            default:
                setChecked(false);
                mCheckBox.setEnabled(true);
        }
    }

    private void setChecked(boolean isChecked) {
        if (isChecked != mCheckBox.isChecked()) {
            // set listener to null, so onCheckedChanged won't be called
            // if the checked status on Switch isn't changed by user click
            if (mValidListener) {
                mCheckBox.setOnCheckedChangeListener(null);
            }
            mCheckBox.setChecked(isChecked);
            if (mValidListener) {
                mCheckBox.setOnCheckedChangeListener(this);
            }
        }
    }

}
