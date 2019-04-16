package com.a1anwang.okble_demo.activity;

import android.os.ParcelUuid;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.a1anwang.okble_demo.views.HexInputPopupWindow;
import com.a1anwang.okble_demo.views.ServiceDataInputPopupWindow;
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
        serverDevice=new OKBLEServerDeviceImp(this);

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
        setTitle("Work as a peripheral");
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_add_service_uuid).setOnClickListener(this);
        findViewById(R.id.btn_add_service_data).setOnClickListener(this);
        findViewById(R.id.tv_company_value).setOnClickListener(this);

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
                okbleAdvertiseManager.stopAdvertising();
                serverDevice.reSet();
                break;
            case R.id.btn_add_service_uuid:
                showInput();
                break;
            case R.id.btn_add_service_data:
                showDataInput();
                break;
            case R.id.tv_company_value:
                showCompanyDataInput();
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
    private void showDataInput() {
        ServiceDataInputPopupWindow popupWindow=new ServiceDataInputPopupWindow(this);
        popupWindow.showAt(findViewById(R.id.rootview), Gravity.BOTTOM,0,0);
        popupWindow.setInputListener(new ServiceDataInputPopupWindow.InputListener() {
            @Override
            public void onInputComplete(String uuid, String value) {
                if((OKBLEDataUtils.isValidShortUUID(uuid)||OKBLEDataUtils.isValidUUID(uuid))&& !TextUtils.isEmpty(value)){
                    addServiceData(uuid,value);
                }
            }
        });
    }
    private void showCompanyDataInput() {

        HexInputPopupWindow popupWindow=new HexInputPopupWindow(this);
        popupWindow.showAt(findViewById(R.id.rootview), Gravity.BOTTOM,0,0);
        popupWindow.setInputListener(new HexInputPopupWindow.InputListener() {
            @Override
            public void onInputComplete(String value) {
                TextView tv= findViewById(R.id.tv_company_value);
                tv.setText(value);
            }
        });
    }

    private void addServiceUUID(String value) {

        final LinearLayout linelayout_service_uuid=findViewById(R.id.linelayout_service_uuid);

        final TextView textView=new TextView(mContext);
        textView.setText(value);

        linelayout_service_uuid.addView(textView,0,new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linelayout_service_uuid.removeView(textView);
            }
        });
    }
    private void addServiceData(String uuid,String value) {

        final LinearLayout linelayout_service_data=findViewById(R.id.linelayout_service_data);

        final TextView textView=new TextView(mContext);
        textView.setText(uuid+":"+value);
        linelayout_service_data.addView(textView,0,new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT ));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linelayout_service_data.removeView(textView);
            }
        });
    }


    private void startAdvertising() {
        Switch switch_connectable=findViewById(R.id.switch_connectable);
        boolean connectable=switch_connectable.isChecked();

        Switch switch_includename=findViewById(R.id.switch_includename);
        boolean includename=switch_includename.isChecked();


        OKBLEAdvertiseSettings settings= new OKBLEAdvertiseSettings.Builder().setConnectable(connectable).build();

        OKBLEAdvertiseData.Builder dataBuilder=new OKBLEAdvertiseData.Builder().setIncludeDeviceName(includename);

        LinearLayout linelayout_service_uuid=findViewById(R.id.linelayout_service_uuid);
        int service_uuid_count=linelayout_service_uuid.getChildCount()-1;
        for (int i=0;i<service_uuid_count;i++){
            TextView tv= (TextView) linelayout_service_uuid.getChildAt(i);
            ParcelUuid uuid=CommonUUIDUtils.createUUIDByShortOrCompleteUUIDStr(tv.getText().toString());
            if(uuid!=null){
                dataBuilder.addServiceUuid(uuid);
            }
        }

        LinearLayout linelayout_service_data=findViewById(R.id.linelayout_service_data);
        int service_data_count=linelayout_service_data.getChildCount()-1;
        for (int i=0;i<service_data_count;i++){
            TextView tv= (TextView) linelayout_service_data.getChildAt(i);

            String[] strings=tv.getText().toString().split(":");
            if(strings.length==2){
                String uuidStr=strings[0];
                String dataStr=strings[1];
                ParcelUuid uuid=CommonUUIDUtils.createUUIDByShortOrCompleteUUIDStr(uuidStr);
                if(uuid!=null){
                    dataBuilder.addServiceData(uuid,OKBLEDataUtils.hexStringToBytes(dataStr));
                }
            }
        }

        TextView tv_company_id=findViewById(R.id.tv_company_id);

        TextView tv_company_value=findViewById(R.id.tv_company_value);
        String company_id=tv_company_id.getText().toString();
        String company_value=tv_company_value.getText().toString();

        if(!TextUtils.isEmpty(company_id)&&!TextUtils.isEmpty(company_value)&&TextUtils.isDigitsOnly(company_id)){
            dataBuilder.addManufacturerData(Integer.valueOf(company_id),OKBLEDataUtils.hexStringToBytes(company_value));
        }

        OKBLEAdvertiseData okbleAdvertiseData=dataBuilder.build();
        okbleAdvertiseManager.startAdvertising(settings, okbleAdvertiseData, new OKBLEAdvertiseCallback() {
            @Override
            public void onStartSuccess() {
                LogUtils.e("---onStartSuccess ---");
                Toast.makeText(mContext,"Advertising Success",Toast.LENGTH_SHORT).show();
                //configServer();
            }
            @Override
            public void onStartFailure(int errorCode, String errMsg) {
                LogUtils.e("---onStartFailure errMsg:"+errMsg);
                Toast.makeText(mContext,"Advertising Failed:"+errMsg,Toast.LENGTH_LONG).show();

            }
        });
    }

    private void configServer(){


        OKBLEServiceModel serviceModel=new OKBLEServiceModel(CommonUUIDUtils.createCompleteUUIDByShortUUID("fff0"));

        OKBLECharacteristicModel characteristicModel=new OKBLECharacteristicModel(CommonUUIDUtils.createCompleteUUIDByShortUUID("fff1"));
        characteristicModel.setCanWrite(true);
        characteristicModel.setCanNotify(true);
        characteristicModel.setCanRead(true);

        OKBLECharacteristicModel characteristicModel_2=new OKBLECharacteristicModel(CommonUUIDUtils.createCompleteUUIDByShortUUID("fff2"));
        characteristicModel_2.setCanWriteNoResponse(true);

        List<OKBLECharacteristicModel> characteristicModels=new ArrayList<>();
        characteristicModels.add(characteristicModel);
        characteristicModels.add(characteristicModel_2);

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
