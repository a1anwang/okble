package com.a1anwang.okble.beacon;

import android.content.Context;

import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseCallback;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseData;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseManager;
import com.a1anwang.okble.server.advertise.OKBLEAdvertiseSettings;

/**
 * Created by a1anwang.com on 2018/6/1.
 */

public class OKBLEBeBeaconManager {
    private Context mContext;
    private OKBLEAdvertiseManager advertiseManager;

    private  OKBLEStartBeaconListener listener;

    public void setOKBLEBeBeaconListener(OKBLEStartBeaconListener listener) {
        this.listener = listener;
    }

    public OKBLEBeBeaconManager(Context context){
        this.mContext=context;
        init();
    }

    private void init() {
        advertiseManager=new OKBLEAdvertiseManager(mContext);

    }

    public void startIBeacon(String uuid,int major,int minor ){
        OKBLEAdvertiseSettings advertiseSettings=new OKBLEAdvertiseSettings.Builder().setConnectable(false).build();
        OKBLEAdvertiseData.Builder dataBuilder=new OKBLEAdvertiseData.Builder();

        String beaconType="0215"; //按照apple iBeacon协议
        uuid=uuid.replace("-","");
        String majorStr= OKBLEDataUtils.formatStringLenth(4,Integer.toHexString(major),'0');
        String minorStr=OKBLEDataUtils.formatStringLenth(4,Integer.toHexString(minor),'0');
        String measuredPower=OKBLEDataUtils.formatStringLenth(2,Integer.toHexString(-59),'0');//-59是 measuredPower,一般设备默认都是-59，这里固定了


        LogUtils.e(" measuredPower:"+measuredPower +"   "+Integer.toHexString(-59));
        String dataStr=beaconType+uuid+majorStr+minorStr+measuredPower;
        LogUtils.e(" dataStr:"+dataStr);
        byte[] data= OKBLEDataUtils.hexStringToBytes( dataStr);

        dataBuilder.addManufacturerData(0x004C,data);//004c是apple 公司id
        advertiseManager.startAdvertising(advertiseSettings, dataBuilder.build(), new OKBLEAdvertiseCallback() {
            @Override
            public void onStartSuccess() {
                if(listener!=null){
                    listener.onStartSuccess();
                }
            }

            @Override
            public void onStartFailure(int errorCode, String errMsg) {
                if(listener!=null){
                    listener.onStartFailure(errMsg);
                }
            }
        });
    }
    public void  stop(){
        advertiseManager.stopAdvertising();
    }

    public interface OKBLEStartBeaconListener {

        void onStartSuccess();

        void onStartFailure(String errMsg);
    }


}
