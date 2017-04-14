package com.lhy.settings.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhy.settings.R;
import com.lhy.settings.bluetooth.BluetoothEnabler;
import com.lhy.settings.bluetooth.BluetoothSettings;
import com.lhy.settings.bluetooth.DeviceProfilesSettings;

/**
 * Created by lhy on 2017/4/14
 */
public class BluetoothFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "BluetoothFragment";
    public static final int FRAGMENT_BLUETOOTH = 10;
    private RelativeLayout rl_searchDevices;//搜索蓝牙
    private RelativeLayout rl_bluetooth;//蓝牙菜单
    private FragmentManager mFragmentManager;
    private Context mContext;
    private BluetoothEnabler mBluetoothEnabler;
    private BluetoothSettings mBluetoothSettings;//蓝牙列表
    private DeviceProfilesSettings profilesSettings;//已配对设备详情
    private CheckBox cb_blueOnOff;//蓝牙开关
    private TextView tv_menue;
    private TextView tv_search;
    private ImageView iv_search;
    private final String BLUETOOTH_TAG = "bluetooth";
    private final String PROFILES_TAG = "DeviceProfilesSettings";

    @Override
    public void onClick(View view) {
        mBluetoothSettings.setOnclickView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mBluetoothEnabler.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mBluetoothEnabler.pause();
    }

    public interface IOnClickListener {
        void onClick(Bundle args);

        void bluetoothStateChange(boolean bluetoothIsEnabled, boolean isDiscovering, int textId);
    }

    /**
     * 对外部提供该Fragment实例
     */
    public static BluetoothFragment getInstance() {
        return getInstance(null);
    }

    /**
     * 对外部提供该Fragment实例
     */
    public static BluetoothFragment getInstance(Bundle _bundle) {
        BluetoothFragment fragment = new BluetoothFragment();
        if (_bundle != null) {
            Bundle bundle = _bundle;
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void initBluetooth() {
        mBluetoothEnabler = new BluetoothEnabler(mContext, new CheckBox(mContext));
        mBluetoothEnabler.setCheckBox(cb_blueOnOff);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = View.inflate(getActivity(), R.layout.fragment_bluetooth_new, null);
        initView(view);

//        if (mBluetoothSettings == null) {
//            mBluetoothSettings = new BluetoothSettings();
//        }
//        mBluetoothSettings.setOnClickListener(new IOnClickListener() {
//            @Override
//            public void onClick(Bundle args) {
//                changeFragment(args);
//            }
//
//            @Override
//            public void bluetoothStateChange(boolean bluetoothIsEnabled, boolean isDiscovering, int textId) {
//                Log.i(TAG, "bluetoothIsEnabled=" + bluetoothIsEnabled + " isDiscovering=" + isDiscovering + " textId=" + textId);
//                int menuId = bluetoothIsEnabled ? R.mipmap.menu : R.mipmap.menu_gray;
//                tv_menue.setEnabled(bluetoothIsEnabled);
//                tv_menue.setBackgroundResource(menuId);
//
//                tv_search.setText(textId);
//                animatinState(isDiscovering);
//                int searchId = bluetoothIsEnabled ? R.mipmap.refresh_true : R.mipmap.refresh_gray;
//                iv_search.setBackgroundResource(searchId);
//                rl_searchDevices.setEnabled(bluetoothIsEnabled && !isDiscovering);
//
//            }
//        });
//
//        mContext = getActivity();
//        initBluetooth();
//        try {
//            mFragmentManager = getActivity().getFragmentManager();
//            mFragmentManager.beginTransaction().replace(R.id.fl_content_fragmentBT, mBluetoothSettings, BLUETOOTH_TAG).commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        rl_searchDevices.setOnClickListener(this);
//        tv_menue.setOnClickListener(this);
        return view;
    }

    private void initView(View view) {
        rl_searchDevices = (RelativeLayout) view.findViewById(R.id.rl_searchDevices);
        rl_bluetooth = (RelativeLayout) view.findViewById(R.id.rl_bluetooth);
        iv_search = (ImageView) view.findViewById(R.id.iv_search);
        cb_blueOnOff = (CheckBox) view.findViewById(R.id.cb_blueOnOff);
        tv_menue = (TextView) view.findViewById(R.id.tv_menue);
        tv_search = (TextView) view.findViewById(R.id.tv_search);




    }

    Animation mAnimation;

    /**
     * 开启或停止搜索按钮动画
     *
     * @param bool
     */
    private void animatinState(boolean bool) {
        if (bool) {
            if (mAnimation == null) {
                mAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.loading_anim_bluetooth_search);
                LinearInterpolator lin = new LinearInterpolator();
                mAnimation.setInterpolator(lin);
            }
            iv_search.startAnimation(mAnimation);
        } else {

            iv_search.clearAnimation();
        }

    }

    /**
     * 改变当前Fragment
     */
    private void changeFragment(Bundle args) {
        if (args == null) {
            //切换到蓝牙界面
            rl_bluetooth.setVisibility(View.VISIBLE);
            mFragmentManager.beginTransaction().replace(R.id.fl_content_fragmentBT, mBluetoothSettings, BLUETOOTH_TAG).commit();
        } else {
            //切换到已配对设备详情界面
            if (profilesSettings == null) {
                profilesSettings = new DeviceProfilesSettings();//已配对设备详情
            }
            profilesSettings.setOnClickListener(new IOnClickListener() {
                @Override
                public void onClick(Bundle args) {
                    changeFragment(args);
                }

                @Override
                public void bluetoothStateChange(boolean bluetoothIsEnabled, boolean isDiscovering, int textId) {
                }
            });
            rl_bluetooth.setVisibility(View.GONE);
            profilesSettings.setArguments(args);
            mFragmentManager.beginTransaction().replace(R.id.fl_content_fragmentBT, profilesSettings, PROFILES_TAG).commit();
        }
    }

    public void removeBluetoothFragment() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mFragmentManager.findFragmentByTag(BLUETOOTH_TAG) != null) {
            Log.i(TAG, "移除蓝牙列表Fragment");
            transaction.remove(mFragmentManager.findFragmentByTag(BLUETOOTH_TAG));
        }
        if (mFragmentManager.findFragmentByTag(PROFILES_TAG) != null) {
            Log.i(TAG, "移除蓝牙配对Fragment");
            transaction.remove(mFragmentManager.findFragmentByTag(PROFILES_TAG));
        }
        transaction.commit();

    }
}
