package com.lhy.settings;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Created by chirenjie on 2016/11/25.
 */
public abstract class BluetoothSettingsDialog extends BaseDialog {
    private TextView mRenameText, mTimeoutText;

    public BluetoothSettingsDialog(Context context) {
        super(context, R.style.WifiAspDialog);
        setContentView(R.layout.settings_rename_layout_dialog);
        mRenameText = (TextView) findViewById(R.id.rename_device);
        mTimeoutText = (TextView) findViewById(R.id.timeout_settings);
        mRenameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onRenameClick();
            }
        });
        mTimeoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onTimeoutClick();
            }
        });
    }

    public abstract void onRenameClick();

    public abstract void onTimeoutClick();
}
