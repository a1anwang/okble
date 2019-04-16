
package com.a1anwang.okble.beacon;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import com.a1anwang.okble.client.scan.BLEScanResult;
import com.a1anwang.okble.client.scan.DeviceScanCallBack;
import com.a1anwang.okble.client.scan.OKBLEScanManager;
import com.a1anwang.okble.common.OKBLEDataUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class OKBLEBeaconManager {
    private Context mContext;
    private OKBLEScanManager okbleScanManager;

    private OKBLEBeaconScanCallback scanCallback;

    private OKBLEBeaconRegionListener regionListener;

    private Map<String, RegionObject> monitoringBeaconRegions = new HashMap<String, RegionObject>();

    private final int regionExitOverTime = 10 * 1000;//退出区域的超时时间，持续regionExitOverTime这么长的时间内没有再次扫描到这个区域，则视为退出区域

    public void setBeaconScanCallback(OKBLEBeaconScanCallback mCallback){
        this.scanCallback=mCallback;
    }

    public void setRegionListener(OKBLEBeaconRegionListener regionListener){
        this.regionListener=regionListener;
    }

    public OKBLEBeaconManager(Context context){
        super();
        this.mContext=context;
        init();
    }

    private void init() {
        okbleScanManager=new OKBLEScanManager(mContext);
        okbleScanManager.setScanCallBack(new DeviceScanCallBack() {
            @Override
            synchronized public void onBLEDeviceScan(BLEScanResult device, int rssi) {
                byte[] beaconData=formatIBeaconData(device);
                if(beaconData!=null){
                    //扫描到iBeacon设备
                    OKBLEBeacon okbleBeacon=new OKBLEBeacon();
                    okbleBeacon.setDevice(device.getBluetoothDevice());
                    okbleBeacon.setUuid(formatUUIDFromIBeaconData(beaconData));
                    okbleBeacon.setMajor(formatMajorFromIBeaconData(beaconData));
                    okbleBeacon.setMinor(formatMinorFromIBeaconData(beaconData));
                    okbleBeacon.setRssi(rssi);
                    okbleBeacon.setMeasuredPower(formatMeasuredPowerFromIBeaconData(beaconData));
                    okbleBeacon.setName(device.getBluetoothDevice().getName());
                    if(scanCallback!=null){
                        scanCallback.onScanBeacon(okbleBeacon);
                    }


                    if(monitoringBeaconRegions.size()>0 ){
                        String key=okbleBeacon.getIdentifier();

                        if(monitoringBeaconRegions.containsKey(key)){
                            //如果正在监控这个区域
                            RegionObject regionObject=monitoringBeaconRegions.get(key);
                            handleEnterRegion(regionObject);
                        }

                        key=okbleBeacon.getUuid()+"_-1_-1";//这里的-1是对应了OKBLEBeaconRegion里的major，minor的默认值为-1
                        if (monitoringBeaconRegions.containsKey(key)) {
                            //如果正在监控这个区域
                            RegionObject regionObject=monitoringBeaconRegions.get(key);
                            handleEnterRegion(regionObject);
                        }
                        key = okbleBeacon.getUuid() + "_" + okbleBeacon.getMajor() + "_-1";
                        if (monitoringBeaconRegions.containsKey(key)) {
                            //如果正在监控这个区域
                            RegionObject regionObject=monitoringBeaconRegions.get(key);
                            handleEnterRegion(regionObject);
                        }
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



    /**
     * 开始扫描iBeacon
     */
    public void startScanBeacon(){
        okbleScanManager.stopScan();
        okbleScanManager.startScan();
    }

    public boolean isScanning(){
        return okbleScanManager.isScanning();
    }

    /**
     * 停止扫描iBeacon
     */
    public void stopScan(){
        okbleScanManager.stopScan();
        handler.removeCallbacksAndMessages(null);
    }

    private int monitoringBeaconRegionID = 0;//监控的iBeacon区域的id

    public void startMonitoringForRegion(OKBLEBeaconRegion region) {
        String key = region.getIdentifier();
        if (!monitoringBeaconRegions.containsKey(key)) {
            monitoringBeaconRegionID++;

            RegionObject regionObject=new RegionObject(region,monitoringBeaconRegionID);

            monitoringBeaconRegions.put(key, regionObject);
            if(!isScanning()){
                startScanBeacon();
            }
        }
    }
    public void stopMonitoringForRegion(OKBLEBeaconRegion region){
        String key = region.getIdentifier();
        monitoringBeaconRegions.remove(key);
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
        if(manufacturerData==null||manufacturerData.size()==0){
            return null;
        }
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

    private void handleEnterRegion(RegionObject regionObject) {
        handler.removeMessages(regionObject.regionID);//移除超时时间后回调退出区域的消息
        Message msg = new Message();
        msg.what = regionObject.regionID;
        msg.obj = regionObject.region.getIdentifier();
        handler.sendMessageDelayed(msg,regionExitOverTime);//重新发送一个延时消息，

        if(!regionObject.hasEntered){
            regionObject.hasEntered = true;
            if(regionListener!=null){
                if(okbleScanManager.isScanning()){
                    regionListener.onEnterBeaconRegion(regionObject.region);
                }
            }
        }
    }


    Handler handler= new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String key = (String) msg.obj;
            //收到消息，表示已经持续了一段时间没有扫描到区域内的beacon了，视为退出区域
            if (monitoringBeaconRegions.containsKey(key)) {
                RegionObject regionObject= monitoringBeaconRegions.get(key);
                OKBLEBeaconRegion beaconRegion =regionObject.region;

                regionObject.hasEntered = false;
                if(regionListener!=null){
                    if(okbleScanManager.isScanning()){
                        regionListener.onExitBeaconRegion(beaconRegion);
                    }
                }
            }
        }
    };

    public void requestLocationPermission(){
        if(okbleScanManager!=null){
            okbleScanManager.requestLocationPermission();
        }
    }

    public interface OKBLEBeaconScanCallback{
        void onScanBeacon(OKBLEBeacon beacon);
    }

    public interface OKBLEBeaconRegionListener{
        void onEnterBeaconRegion(OKBLEBeaconRegion beaconRegion);

        void onExitBeaconRegion(OKBLEBeaconRegion beaconRegion);

        void onRangeBeaconsInRegion(List<OKBLEBeacon> beacons);
    }


    private class RegionObject{
        boolean hasEntered;
        OKBLEBeaconRegion region;
        int regionID;

        public RegionObject(OKBLEBeaconRegion region, int regionID) {
            super();
            this.region=region;
            this.regionID=regionID;
        }
    }

}