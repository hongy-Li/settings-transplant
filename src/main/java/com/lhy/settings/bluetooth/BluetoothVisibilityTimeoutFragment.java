/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.lhy.settings.BaseDialog;
import com.lhy.settings.R;


/**
 * Dialog fragment for setting the discoverability timeout.
 */
public final class BluetoothVisibilityTimeoutFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

    private final BluetoothDiscoverableEnabler mDiscoverableEnabler;

    public BluetoothVisibilityTimeoutFragment() {
        mDiscoverableEnabler = LocalBluetoothManager.getInstance(getActivity())
                .getDiscoverableEnabler();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimeoutDialog timeoutDialog = new TimeoutDialog(getActivity()) {
            @Override
            public void onItemClick(int posion) {
                mDiscoverableEnabler.setDiscoverableTimeout(posion);
                dismiss();
            }
        };
        Log.d("TAG", "onCreateDialog: " + mDiscoverableEnabler.getDiscoverableTimeoutIndex());
        timeoutDialog.setRadioButtonCheck(mDiscoverableEnabler.getDiscoverableTimeoutIndex());
        return timeoutDialog;
    }

    public void onClick(DialogInterface dialog, int which) {

    }

    private abstract class TimeoutDialog extends BaseDialog implements View.OnClickListener {
        private RadioButton[] radioButtons = new RadioButton[4];
        private String[] contents;
        private int checkPosition;

        public TimeoutDialog(Context context) {
            super(context, R.style.WifiAspDialog);
            setContentView(R.layout.timeout_layout);
            contents = getResources().getStringArray(R.array.bluetooth_visibility_timeout_entries);
            radioButtons[0] = (RadioButton) findViewById(R.id.radio_0);
            radioButtons[1] = (RadioButton) findViewById(R.id.radio_1);
            radioButtons[2] = (RadioButton) findViewById(R.id.radio_2);
            radioButtons[3] = (RadioButton) findViewById(R.id.radio_3);
            radioButtons[0].setText(contents[0]);
            findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            for (int i = 0; i < radioButtons.length; i++) {
                radioButtons[i].setText(contents[i]);
                radioButtons[i].setOnClickListener(this);
            }
        }

        public void setRadioButtonCheck(int checkPosition) {
            this.checkPosition = checkPosition;

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("TAG", "onCreate: " + checkPosition);
            radioButtons[checkPosition].setChecked(true);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.radio_0:
                    onItemClick(0);
                    break;
                case R.id.radio_1:
                    onItemClick(1);
                    break;
                case R.id.radio_2:
                    onItemClick(2);
                    break;
                case R.id.radio_3:
                    onItemClick(3);
                    break;
            }
        }

        public abstract void onItemClick(int posion);
    }
}
