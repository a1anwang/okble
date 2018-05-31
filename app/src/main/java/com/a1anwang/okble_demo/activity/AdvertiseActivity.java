package com.a1anwang.okble_demo.activity;

import android.os.ParcelUuid;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a1anwang.okble.common.CommonUUIDUtils;
import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble.common.OKBLECharacteristicModel;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble.common.OKBLEServiceModel;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseCallback;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseData;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseManager;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseSettings;
import com.a1anwang.okble.server.core.OKBLEServerDevice;
import com.a1anwang.okble.server.core.OKBLEServerDeviceImp;
import com.a1anwang.okble.server.core.OKBLEServerOperation;
import com.a1anwang.okble_demo.R;
import com.a1anwang.okble_demo.base.BaseActivity;
import com.a1anwang.okble_demo.views.ServiceUUIDInputPopupWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class AdvertiseActivity extends BaseActivity{
    OKBLEAdvertiseManager okbleAdvertiseManager;
    OKBLEServerDevice serverDevice;
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
    protected void onDestroy() {
        super.onDestroy();
        okbleAdvertiseManager.stopAdvertising();
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
        findViewById(R.id.btn_add_service_uuid).setOnClickListener(this);

    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void onClickEvent(View v) {
        switch (v.getId()){
            case R.id.btn_start:
                OKBLEAdvertiseSettings settings= new OKBLEAdvertiseSettings.Builder().setConnectable(true).build();
                OKBLEAdvertiseData okbleAdvertiseData=new OKBLEAdvertiseData.Builder().setIncludeDeviceName(false).addServiceData(ParcelUuid.fromString("12345678-0000-1000-8000-00805f9b34fc"),new byte[]{(byte) 0xdd}).addServiceData(ParcelUuid.fromString("1234abcd-0000-1000-8000-00805f9b34fc"),new byte[]{(byte) 0xcc}).build();
                okbleAdvertiseManager.startAdvertising(settings, okbleAdvertiseData, new OKBLEAdvertiseCallback() {
                    @Override
                    public void onStartSuccess() {
                        LogUtils.e("---onStartSuccess ---");
                        configServer();
                    }
                    @Override
                    public void onStartFailure(int errorCode, String errMsg) {
                        LogUtils.e("---onStartFailure errMsg:"+errMsg);
                    }
                });
                break;
            case R.id.btn_stop:
                okbleAdvertiseManager.stopAdvertising();
                serverDevice.reSet();
                break;
            case R.id.btn_add_service_uuid:
                showInput();
                break;
        }
    }

    private void showInput() {
        ServiceUUIDInputPopupWindow popupWindow=new ServiceUUIDInputPopupWindow(this);
        popupWindow.showAt(findViewById(R.id.rootview), Gravity.BOTTOM,0,0);
        popupWindow.setInputListener(new ServiceUUIDInputPopupWindow.InputListener() {
            @Override
            public void onInputComplete(String value) {
                if(OKBLEDataUtils.isValidShortUUID(value)||OKBLEDataUtils.isValidUUID(value)){
                    addServiceUUID(value);
                }
            }
        });
    }

    private void addServiceUUID(String value) {
        LinearLayout linelayout_service_uuid=findViewById(R.id.linelayout_service_uuid);

        TextView textView=new TextView(mContext);
        textView.setText(value);

        linelayout_service_uuid.addView(textView,0,new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }


    private void configServer(){
        serverDevice=new OKBLEServerDeviceImp(this);

        OKBLEServiceModel serviceModel=new OKBLEServiceModel(CommonUUIDUtils.createUUIDByShortUUID("fff0"));

        OKBLECharacteristicModel characteristicModel=new OKBLECharacteristicModel(CommonUUIDUtils.createUUIDByShortUUID("fff1"));
        characteristicModel.setCanWrite(true);
        characteristicModel.setCanWriteNoResponse(true);
        characteristicModel.setCanNotify(true);
        characteristicModel.setCanRead(true);



        OKBLECharacteristicModel characteristicModel_2=new OKBLECharacteristicModel(CommonUUIDUtils.createUUIDByShortUUID("fff2"));
        characteristicModel_2.setCanWrite(true);
        characteristicModel_2.setCanWriteNoResponse(true);
        characteristicModel_2.setCanNotify(true);
        characteristicModel_2.setCanIndicate(true);


        OKBLECharacteristicModel characteristicModel_3=new OKBLECharacteristicModel(CommonUUIDUtils.createUUIDByShortUUID("fff3"));
        characteristicModel_3.setCanWrite(true);
        characteristicModel_3.setCanWriteNoResponse(true);

        List<OKBLECharacteristicModel> characteristicModels=new ArrayList<>();
        characteristicModels.add(characteristicModel);
        characteristicModels.add(characteristicModel_2);
        characteristicModels.add(characteristicModel_3);


        serverDevice.addCharacteristic(characteristicModels, serviceModel, new OKBLEServerOperation.BLEServerOperationListener() {
            @Override
            public void onAddCharacteristicFailed(int errorCode, String errorMsg) {
                LogUtils.e("onAddCharacteristicFailed:"+errorMsg );
            }

            @Override
            public void onAddCharacteristicSuccess() {
                LogUtils.e("onAddCharacteristicSuccess");

            }
        });

    }
}
