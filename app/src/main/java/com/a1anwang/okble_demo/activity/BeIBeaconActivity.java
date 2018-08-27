package com.a1anwang.okble_demo.activity;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.a1anwang.okble.beacon.OKBLEBeBeaconManager;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble_demo.R;
import com.a1anwang.okble_demo.base.BaseActivity;
import com.a1anwang.okble_demo.views.CompleteUUIDInputPopupWindow;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class BeIBeaconActivity extends BaseActivity{
    OKBLEBeBeaconManager beBeaconManager;


    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_be_ibeacon);
    }

    @Override
    public void beforeInitView() {
        beBeaconManager=new OKBLEBeBeaconManager(this);
        beBeaconManager.setOKBLEBeBeaconListener(startBeaconListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beBeaconManager.stop();
    }

    @Override
    public void initView() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        setHeadVisibility(View.VISIBLE);
        setTitle("Work as an iBeacon");
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.layout_uuid).setOnClickListener(this);


    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void onClickEvent(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                startAdvertising();
                break;
            case R.id.btn_stop:
                beBeaconManager.stop();
                break;
            case R.id.layout_uuid:
                showHexInput();
                break;
        }
    }




    private void showHexInput() {

        CompleteUUIDInputPopupWindow popupWindow=new CompleteUUIDInputPopupWindow(this);
        popupWindow.showAt(findViewById(R.id.rootview), Gravity.BOTTOM,0,0);
        popupWindow.setInputListener(new CompleteUUIDInputPopupWindow.InputListener() {
            @Override
            public void onInputComplete(String value) {
                EditText edit_uuid= findViewById(R.id.edit_uuid);
                edit_uuid.setText(value);
            }
        });
    }


    private void startAdvertising() {
        EditText edit_uuid= findViewById(R.id.edit_uuid);
        EditText edit_major= findViewById(R.id.edit_major);
        EditText edit_minor= findViewById(R.id.edit_minor);

        String uuid =edit_uuid.getText().toString().toUpperCase();



        boolean isvaliduuid= OKBLEDataUtils.isValidUUID(uuid);
        if(!isvaliduuid){
            Toast.makeText(this,"UUID is not valid",Toast.LENGTH_SHORT).show();
            return;
        }
        int major = -1;
        if (!TextUtils.isEmpty(edit_major.getText().toString())) {
            major = Integer.parseInt(edit_major.getText().toString());
        }
        int minor = -1;
        if (!TextUtils.isEmpty(edit_minor.getText().toString())) {
            minor = Integer.parseInt(edit_minor.getText().toString());
        }
        if (major < 0 || major > 65535) {
            Toast.makeText(this,"major ranges：[0- 65535]",Toast.LENGTH_SHORT).show();
            return;

        }
        if (minor < 0 || minor > 65535) {
            Toast.makeText(this,"minor ranges：[0- 65535]",Toast.LENGTH_SHORT).show();
            return;
        }

        beBeaconManager.startIBeacon(uuid,major,minor);
    }
    OKBLEBeBeaconManager.OKBLEStartBeaconListener startBeaconListener=new OKBLEBeBeaconManager.OKBLEStartBeaconListener() {
            @Override
            public void onStartSuccess() {
                Toast.makeText(mContext,"start success",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStartFailure(String errMsg) {
                Toast.makeText(mContext,"start failed:"+errMsg,Toast.LENGTH_SHORT).show();

            }
    };
}
