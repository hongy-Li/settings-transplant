package com.lhy.settings.bluetooth;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lhy.settings.BaseDialog;
import com.lhy.settings.R;


/**
 * Created by chirenjie on 2016/11/24.
 */
public abstract class RenameDialog extends BaseDialog {
    private Button mDone;
    private EditText mEditText;
    private TextView mTitle;

    public RenameDialog(Context context) {
        super(context, R.style.WifiAspDialog);
        setContentView(R.layout.rename_layout);
        mEditText = (EditText) findViewById(R.id.edittext);
        mTitle = (TextView) findViewById(R.id.title);
        mDone = (Button) findViewById(R.id.done);
        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    submitBtn(mEditText.getText().toString().trim());
                    dismiss();
                }
            }
        });
        findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setDeviceName(String deviceName) {
        mEditText.setText(deviceName);
    }

    public Button getDone() {
        return mDone;
    }

    public abstract void submitBtn(String name);
}
