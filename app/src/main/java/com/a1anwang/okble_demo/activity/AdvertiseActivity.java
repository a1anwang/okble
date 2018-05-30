package com.a1anwang.okble_demo.activity;

import android.support.v7.app.ActionBar;
import android.view.View;

import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseCallback;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseData;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseManager;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseSettings;
import com.a1anwang.okble_demo.R;
import com.a1anwang.okble_demo.base.BaseActivity;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class AdvertiseActivity extends BaseActivity{
    OKBLEAdvertiseManager okbleAdvertiseManager;

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_advertise);
    }

    @Override
    public void beforeInitView() {
        okbleAdvertiseManager=new OKBLEAdvertiseManager(this);

    }

    @Override
    public void initView() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        setHeadVisibility(View.VISIBLE);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);


    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void onClickEvent(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                OKBLEAdvertiseSettings settings= new OKBLEAdvertiseSettings.Builder().setConnectable(true).build();
                OKBLEAdvertiseData okbleAdvertiseData=new OKBLEAdvertiseData.Builder().setIncludeDeviceName(true).build();
                okbleAdvertiseManager.startAdvertising(settings, okbleAdvertiseData, new OKBLEAdvertiseCallback() {
                    @Override
                    public void onStartSuccess() {
                        LogUtils.e("---onStartSuccess ---");
                    }
                    @Override
                    public void onStartFailure(int errorCode, String errMsg) {
                        LogUtils.e("---onStartFailure errMsg:"+errMsg);
                    }
                });
                break;
            case R.id.btn_stop:
                okbleAdvertiseManager.stopAdvertising();
                break;
        }
    }
}
