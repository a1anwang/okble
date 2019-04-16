package com.a1anwang.okble.server.advertise;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;

import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble.common.OKBLEDataUtils;

import java.util.Set;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class OKBLEAdvertiseManager {

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private Context context;
    public OKBLEAdvertiseManager(Context context){
        this.context=context;
        init();
    }

    private void init() {
        bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

    }

    Object mAdvertiseCallback;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertising(OKBLEAdvertiseSettings okbleAdvertiseSettings, OKBLEAdvertiseData data, final OKBLEAdvertiseCallback okbleAdvertiseCallback){
        stopAdvertising();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if(!bluetoothAdapter.isMultipleAdvertisementSupported()){
//                if(okbleAdvertiseCallback!=null){
//                    okbleAdvertiseCallback.onStartFailure(OKBLEAdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED,OKBLEAdvertiseFailedDescUtils.getDesc(OKBLEAdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED));
//                }
//                return;
//            }
            if(mBluetoothLeAdvertiser==null){
                mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            }

            if(mBluetoothLeAdvertiser!=null){
                AdvertiseSettings mSettings=new AdvertiseSettings.Builder().setConnectable(okbleAdvertiseSettings.isConnectable()).setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY).setTimeout(0).setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH).build();

                AdvertiseData.Builder advertiseDataBuilder=new AdvertiseData.Builder().setIncludeDeviceName(data.getIncludeDeviceName());
                int size_1=data.getManufacturerSpecificData().size();
                for (int i=0;i<size_1;i++){
                    int key=data.getManufacturerSpecificData().keyAt(i);
                    byte[] value=data.getManufacturerSpecificData().get(key);
                    advertiseDataBuilder.addManufacturerData(key,value);
                    LogUtils.e(" Manufacturer id:"+key+" data:"+OKBLEDataUtils.BytesToHexString(value));
                }
                int size_2=data.getServiceUuids().size();
                for (int i=0;i<size_2;i++){
                    ParcelUuid uuid=data.getServiceUuids().get(i);
                    advertiseDataBuilder.addServiceUuid(uuid);
                    LogUtils.e(" service uuid:"+uuid.getUuid().toString());
                }

                Set<ParcelUuid> keySet= data.getServiceData().keySet();
                for (ParcelUuid key_uuid:keySet){
                    LogUtils.e(" service data uuid:"+key_uuid.getUuid().toString()+" data:"+ OKBLEDataUtils.BytesToHexString(data.getServiceData().get(key_uuid)));
                    advertiseDataBuilder.addServiceData(key_uuid,data.getServiceData().get(key_uuid));
                }

                AdvertiseData mAdvertiseData=advertiseDataBuilder.build();

                mAdvertiseCallback=new AdvertiseCallback() {
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        if(okbleAdvertiseCallback!=null){
                            okbleAdvertiseCallback.onStartSuccess();
                        }
                    }
                    @Override
                    public void onStartFailure(int errorCode) {
                        if(okbleAdvertiseCallback!=null){
                            okbleAdvertiseCallback.onStartFailure(errorCode,OKBLEAdvertiseFailedDescUtils.getDesc(errorCode));
                        }
                    }
                };

                mBluetoothLeAdvertiser.startAdvertising(mSettings, mAdvertiseData, (AdvertiseCallback) mAdvertiseCallback);
            }else{
                if(okbleAdvertiseCallback!=null){
                    okbleAdvertiseCallback.onStartFailure(OKBLEAdvertiseCallback.ADVERTISE_FAILED_NULL_ADVERTISER,OKBLEAdvertiseFailedDescUtils.getDesc(OKBLEAdvertiseCallback.ADVERTISE_FAILED_NULL_ADVERTISER));
                }

            }
        }else{
            if(okbleAdvertiseCallback!=null){
                okbleAdvertiseCallback.onStartFailure(OKBLEAdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED,OKBLEAdvertiseFailedDescUtils.getDesc(OKBLEAdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED));
            }

        }
    }



    public void stopAdvertising(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(mBluetoothLeAdvertiser!=null){
                mBluetoothLeAdvertiser.stopAdvertising((AdvertiseCallback) mAdvertiseCallback);
            }
        }
    }
}
