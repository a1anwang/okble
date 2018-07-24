package com.a1anwang.okble.beacon;

import android.content.Context;
import android.util.SparseArray;

import com.a1anwang.okble.client.scan.BLEScanResult;
import com.a1anwang.okble.client.scan.DeviceScanCallBack;
import com.a1anwang.okble.client.scan.OKBLEScanManager;
import com.a1anwang.okble.common.OKBLEDataUtils;

/**
 * Created by a1anwang.com on 2018/5/30.
 */
//纳尼
public class OKBLEBeaconScanManager {
    private Context mContext;
    private OKBLEScanManager okbleScanManager;

    private OKBLEBeaconScanCallback callback;

    public void setBeaconScanCallback(OKBLEBeaconScanCallback mCallback){
        this.callback=mCallback;
    }

    public OKBLEBeaconScanManager(Context context){
        super();
        this.mContext=context;
        init();
    }

    private void init() {
        okbleScanManager=new OKBLEScanManager(mContext);
        okbleScanManager.setScanCallBack(new DeviceScanCallBack() {
            @Override
            public void onBLEDeviceScan(BLEScanResult device, int rssi) {
                byte[] beaconData=formatIBeaconData(device);
                if(beaconData!=null){
                    OKBLEBeacon okbleBeacon=new OKBLEBeacon();
                    okbleBeacon.setDevice(device.getBluetoothDevice());
                    okbleBeacon.setUuid(formatUUIDFromIBeaconData(beaconData));
                    okbleBeacon.setMajor(formatMajorFromIBeaconData(beaconData));
                    okbleBeacon.setMinor(formatMinorFromIBeaconData(beaconData));
                    okbleBeacon.setRssi(rssi);
                    okbleBeacon.setMeasuredPower(formatMeasuredPowerFromIBeaconData(beaconData));
                    okbleBeacon.setName(device.getBluetoothDevice().getName());
                    if(callback!=null){
                        callback.onScanBeacon(okbleBeacon);
                    }
                }
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onStartSuccess() {

            }
        });
    }
    public void startScanBeacon(){
        okbleScanManager.stopScan();
        okbleScanManager.startScan();
    }

    public void stopScan(){
        okbleScanManager.stopScan();
    }


    /**
     * apple iBeacon advertise data protocol:http://blogimages.a1anwang.com/FvAYTEnxU94u-ore6GZBfPWzkSEv-shuiyin2
     * @param scanResult
     * @return
     */
    private byte[] formatIBeaconData(BLEScanResult scanResult){
        if(scanResult.getAdvertisingData()!=null&&scanResult.getAdvertisingData().length==30){//iBeacon advertise data is 30 bytes;
            return null;
        }
        SparseArray<byte[]> manufacturerData= scanResult.getManufacturerSpecificData();
        int size_1=manufacturerData.size();
        for (int i=0;i<size_1;i++){
            int key=manufacturerData.keyAt(i);
            byte[] value=manufacturerData.get(key);
            if(key==0x004C) {//0x004c is apple company id
                if(value!=null && value.length==23&& value[0]==0x02&&value[1]==0x15){//this is an iBeacon
                    return value;
                }
            }
        }

        return null;
    }

    private String formatUUIDFromIBeaconData(byte[] beaconData){
        byte[] uuidValue=new byte[16];
        System.arraycopy(beaconData, 2, uuidValue, 0, 16);
        String uuid="";
        String hexStr= OKBLEDataUtils.BytesToHexString(uuidValue);
        uuid=hexStr.substring(0, 8);
        uuid+="-";
        uuid+=hexStr.substring(8, 12);
        uuid+="-";
        uuid+=hexStr.substring(12, 16);
        uuid+="-";
        uuid+=hexStr.substring(16, 20);
        uuid+="-";
        uuid+=hexStr.substring(20, 32);
        return uuid;
    }

    private int formatMajorFromIBeaconData(byte[] beaconData){

        int major=OKBLEDataUtils.buildUint16(beaconData[18],beaconData[19]);

        return major;
    }


    private int formatMinorFromIBeaconData(byte[] beaconData){
        int minor=OKBLEDataUtils.buildUint16(beaconData[20],beaconData[21]);
        return minor;
    }

    private int formatMeasuredPowerFromIBeaconData(byte[] beaconData){
        int measuredPower= beaconData[22];
        return measuredPower;
    }



    public interface OKBLEBeaconScanCallback{
         void onScanBeacon(OKBLEBeacon beacon);
    }
}
