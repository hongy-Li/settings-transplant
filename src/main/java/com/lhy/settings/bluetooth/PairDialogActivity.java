package com.lhy.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.lhy.settings.R;

import java.util.Locale;

/**
 * Created by chirenjie on 2016/12/5.
 */
public class PairDialogActivity extends Activity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, TextWatcher {
    private TextView mTitleTextView;
    private Button btn1, btn2, btn3;
    private Intent mIntent;
    private BluetoothDevice mDevice;
    private int mType;
    private String mPairingKey;
    private EditText mPairingView;
    private Button mOkButton;
    private LinearLayout mContainerLayout;
    private static final int BLUETOOTH_PIN_MAX_LENGTH = 16;
    private static final int BLUETOOTH_PASSKEY_MAX_LENGTH = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pair_layout);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        mTitleTextView = (TextView) findViewById(R.id.title);
        mIntent = getIntent();
        mContainerLayout = (LinearLayout) findViewById(R.id.container_layout);
        if (!mIntent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
            finish();
            return;
        }

        LocalBluetoothManager manager = LocalBluetoothManager.getInstance(this);
        if (manager == null) {
            finish();
            return;
        }
        CachedBluetoothDeviceManager deviceManager = manager.getCachedDeviceManager();

        mDevice = mIntent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        mType = mIntent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);

        switch (mType) {
            case BluetoothDevice.PAIRING_VARIANT_PIN:
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
                createUserEntryDialog(deviceManager);
                break;

            case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                int passkey =
                        mIntent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, BluetoothDevice.ERROR);
                if (passkey == BluetoothDevice.ERROR) {
//                    Log.e(TAG, "Invalid Confirmation Passkey received, not showing any dialog");
                    return;
                }
                mPairingKey = String.format(Locale.US, "%06d", passkey);
                createConfirmationDialog(deviceManager);
                break;

            case BluetoothDevice.PAIRING_VARIANT_CONSENT:
            case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
                createConsentDialog(deviceManager);
                break;

            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN:
                int pairingKey =
                        mIntent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, BluetoothDevice.ERROR);
                if (pairingKey == BluetoothDevice.ERROR) {
//                    Log.e(TAG, "Invalid Confirmation Passkey or PIN received, not showing any dialog");
                    return;
                }
                if (mType == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY) {
                    mPairingKey = String.format("%06d", pairingKey);
                } else {
                    mPairingKey = String.format("%04d", pairingKey);
                }
                createDisplayPasskeyOrPinDialog(deviceManager);
                break;

            default:
//                Log.e(TAG, "Incorrect pairing type received, not showing any dialog");
        }
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        /*
         * Leave this registered through pause/resume since we still want to
         * finish the activity in the background if pairing is canceled.
         */
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_PAIRING_CANCEL));
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void createDisplayPasskeyOrPinDialog(
            CachedBluetoothDeviceManager deviceManager) {
        DataBean dataBean = new DataBean();
        dataBean.title = getString(R.string.bluetooth_pairing_request);
        dataBean.view = createView(deviceManager);
        dataBean.btn1 = getString(android.R.string.cancel);
        dataBean.showData();
//        // Since its only a notification, send an OK to the framework,
//        // indicating that the dialog has been displayed.
        if (mType == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY) {
            mDevice.setPairingConfirmation(true);
        } else if (mType == BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN) {
            byte[] pinBytes = BluetoothDevice.convertPinToBytes(mPairingKey);
            mDevice.setPin(pinBytes);
        }
    }

    private void createConsentDialog(CachedBluetoothDeviceManager deviceManager) {
        DataBean dataBean = new DataBean();
        dataBean.title = getString(R.string.bluetooth_pairing_request);
        dataBean.view = createView(deviceManager);
        dataBean.btn2 = getString(R.string.bluetooth_pairing_accept);
        dataBean.btn1 = getString(R.string.bluetooth_pairing_decline);
        dataBean.showData();
    }

    private void createConfirmationDialog(CachedBluetoothDeviceManager deviceManager) {
        DataBean dataBean = new DataBean();
        dataBean.title = getString(R.string.bluetooth_pairing_request);
        dataBean.view = createView(deviceManager);
        dataBean.btn2 = getString(R.string.bluetooth_pairing_accept);
        dataBean.btn1 = getString(R.string.bluetooth_pairing_decline);
        dataBean.showData();
    }

    private void createUserEntryDialog(CachedBluetoothDeviceManager deviceManager) {
        DataBean dataBean = new DataBean();
        dataBean.title = getString(R.string.bluetooth_pairing_request);
        dataBean.view = createPinEntryView(deviceManager.getName(mDevice));
        dataBean.btn2 = getString(android.R.string.ok);
        dataBean.btn1 = getString(android.R.string.cancel);
        dataBean.showData();
        btn2.setEnabled(false);
    }

    private View createPinEntryView(String deviceName) {
        View view = getLayoutInflater().inflate(R.layout.bluetooth_pin_entry, null);
        TextView messageView = (TextView) view.findViewById(R.id.message);
        TextView messageView2 = (TextView) view.findViewById(R.id.message_below_pin);
        CheckBox alphanumericPin = (CheckBox) view.findViewById(R.id.alphanumeric_pin);
        mPairingView = (EditText) view.findViewById(R.id.text);
        mPairingView.addTextChangedListener(this);
        alphanumericPin.setOnCheckedChangeListener(this);

        int messageId1;
        int messageId2;
        int maxLength;
        switch (mType) {
            case BluetoothDevice.PAIRING_VARIANT_PIN:
                messageId1 = R.string.bluetooth_enter_pin_msg;
                messageId2 = R.string.bluetooth_enter_pin_other_device;
                // Maximum of 16 characters in a PIN
                maxLength = BLUETOOTH_PIN_MAX_LENGTH;
                break;

            case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
                messageId1 = R.string.bluetooth_enter_passkey_msg;
                messageId2 = R.string.bluetooth_enter_passkey_other_device;
                // Maximum of 6 digits for passkey
                maxLength = BLUETOOTH_PASSKEY_MAX_LENGTH;
                alphanumericPin.setVisibility(View.GONE);
                break;

            default:
//                Log.e(TAG, "Incorrect pairing type for createPinEntryView: " + mType);
                return null;
        }

        // Format the message string, then parse HTML style tags
        String messageText = getString(messageId1, deviceName);
        messageView.setText(Html.fromHtml(messageText));
        messageView2.setText(messageId2);
        mPairingView.setInputType(InputType.TYPE_CLASS_NUMBER);
        mPairingView.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(maxLength)});

        return view;
    }


    private View createView(CachedBluetoothDeviceManager deviceManager) {
        View view = getLayoutInflater().inflate(R.layout.bluetooth_pin_confirm, null);
        String name = deviceManager.getName(mDevice);
        TextView messageView = (TextView) view.findViewById(R.id.message);

        String messageText; // formatted string containing HTML style tags
        switch (mType) {
            case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                messageText = getString(R.string.bluetooth_confirm_passkey_msg,
                        name, mPairingKey);
                break;

            case BluetoothDevice.PAIRING_VARIANT_CONSENT:
            case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
                messageText = getString(R.string.bluetooth_incoming_pairing_msg, name);
                break;

            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN:
                messageText = getString(R.string.bluetooth_display_passkey_pin_msg, name,
                        mPairingKey);
                break;

            default:
                return null;
        }
        messageView.setText(Html.fromHtml(messageText));
        return view;
    }

    public void setIntent(Intent mIntent) {
        this.mIntent = mIntent;
    }

    /**
     * Dismiss the dialog if the bond state changes to bonded or none,
     * or if pairing was canceled for {@link #mDevice}.
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);
                if (bondState == BluetoothDevice.BOND_BONDED ||
                        bondState == BluetoothDevice.BOND_NONE) {
                    finish();
                }
            } else if (BluetoothDevice.ACTION_PAIRING_CANCEL.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null || device.equals(mDevice)) {
                    finish();
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn2:
                if (mPairingView != null) {
                    onPair(mPairingView.getText().toString());
                } else {
                    onPair(null);
                }
                break;
            case R.id.btn1:
            default:
                onCancel();
                break;
        }
        finish();
    }

    private void onCancel() {
        mDevice.cancelPairingUserInput();
    }

    private void onPair(String value) {
        switch (mType) {
            case BluetoothDevice.PAIRING_VARIANT_PIN:
                byte[] pinBytes = BluetoothDevice.convertPinToBytes(value);
                if (pinBytes == null) {
                    return;
                }
                mDevice.setPin(pinBytes);
                break;

            case BluetoothDevice.PAIRING_VARIANT_PASSKEY:
                int passkey = Integer.parseInt(value);
                mDevice.setPasskey(passkey);
                break;

            case BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION:
            case BluetoothDevice.PAIRING_VARIANT_CONSENT:
                mDevice.setPairingConfirmation(true);
                break;

            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PASSKEY:
            case BluetoothDevice.PAIRING_VARIANT_DISPLAY_PIN:
                // Do nothing.
                break;

            case BluetoothDevice.PAIRING_VARIANT_OOB_CONSENT:
                mDevice.setRemoteOutOfBandData();
                break;
            default:
//                Log.e(TAG, "Incorrect pairing type received");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            mPairingView.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            mPairingView.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (btn2 != null) {
            btn2.setEnabled(editable.length() > 0);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            onCancel();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class DataBean {
        private View view;
        private String title;
        private String btn1, btn2, btn3;

        private void showData() {
            mTitleTextView.setText(title);
            mContainerLayout.addView(view);
            if (TextUtils.isEmpty(btn1)) {
                PairDialogActivity.this.btn1.setVisibility(View.GONE);
            } else {
                PairDialogActivity.this.btn1.setText(btn1);
                PairDialogActivity.this.btn1.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(btn2)) {
                PairDialogActivity.this.btn2.setVisibility(View.GONE);
            } else {
                PairDialogActivity.this.btn2.setText(btn2);
                PairDialogActivity.this.btn2.setVisibility(View.VISIBLE);
            }
            if (TextUtils.isEmpty(btn3)) {
                PairDialogActivity.this.btn3.setVisibility(View.GONE);
            } else {
                PairDialogActivity.this.btn3.setText(btn3);
                PairDialogActivity.this.btn3.setVisibility(View.VISIBLE);
            }
        }
    }
}
