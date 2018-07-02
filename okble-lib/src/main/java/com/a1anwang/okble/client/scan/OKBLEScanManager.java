package com.a1anwang.okble.client.scan;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble.permission.PermissionConstants;
import com.a1anwang.okble.permission.PermissionUtils;

import java.util.List;

/**
 * Created by a1anwang.com on 2017/6/26.
 */

public class OKBLEScanManager {
private String TAG="OKBLEScanManager";
    private static final int DefaultScanDuration = 10 * 1000;
    private static final int DefaultSleepDuration = 2 * 1000;


    private int scanDuration = DefaultScanDuration;

    private int sleepDuration = DefaultSleepDuration;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private Context context;
    private DeviceScanCallBack deviceScanCallBack;
    private boolean isScanning = false;

    public boolean isScanning() {
        return isScanning;
    }

    public OKBLEScanManager(Context context) {
        this.context = context;
        init();
    }

    public void setScanCallBack(DeviceScanCallBack scanCallBack) {
        this.deviceScanCallBack = scanCallBack;
    }
    public boolean isSupportBLE(){
        if(Build.VERSION.SDK_INT< Build.VERSION_CODES.JELLY_BEAN_MR2){
            return false;
        }
        if (!context. getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        return true;
    }

    private void init() {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }

        LogUtils.e(TAG," 本地MAC："+bluetoothAdapter.getAddress() +" "+bluetoothAdapter.getName());
    }

    public void setScanDuration(int scanDuration) {
        this.scanDuration = scanDuration;
    }

    public void setSleepDuration(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public BluetoothDevice retrieveBluetoothDeviceWithMac(String mac) {
        if (bluetoothAdapter != null) {
            if (BluetoothAdapter.checkBluetoothAddress(mac)) {
                return bluetoothAdapter.getRemoteDevice(mac);
            }
        }
        return null;
    }

    private Handler handle = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what < 1) { //

                if (isScanning) {
                    stopScan();

                    handle.sendEmptyMessageDelayed(0, sleepDuration);
                } else {
                    doScan();

                    handle.sendEmptyMessageDelayed(0, scanDuration);
                }

            }
        }

    };

    public boolean bluetoothIsEnable() {
        return bluetoothAdapter.isEnabled();
    }

    public void startScan() {
        if(!isSupportBLE()){
            if(callback!=null){
                deviceScanCallBack.onFailed(DeviceScanCallBack.SCAN_FAILED_BLE_NOT_SUPPORT);
            }
            return ;
        }
        if (!bluetoothIsEnable()) {
            if(callback!=null){
                deviceScanCallBack.onFailed(DeviceScanCallBack.SCAN_FAILED_BLUETOOTH_DISABLE);
            }
            return ;
        }


        boolean isGranted=  PermissionUtils.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)||PermissionUtils.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION);
        LogUtils.e("isGranted:"+isGranted);
        if(isGranted){
            if(deviceScanCallBack!=null){
                deviceScanCallBack.onStartSuccess();
            }
            doScan();
        }else{
            PermissionUtils.permission(PermissionConstants.LOCATION).callback(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(List<String> permissionsGranted) {
                    if(deviceScanCallBack!=null){
                        deviceScanCallBack.onStartSuccess();
                    }
                    //权限授权成功
                    doScan();
                }

                @Override
                public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                    //权限被禁止
                    if(!permissionsDeniedForever.isEmpty()){
                        if(deviceScanCallBack!=null){
                            deviceScanCallBack.onFailed(DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE_FOREVER);
                        }
                    }else{
                        if(deviceScanCallBack!=null){
                            deviceScanCallBack.onFailed(DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE);
                        }
                    }
                }
            }).request();
        }
    }

    private void doScan(){
        LogUtils.e("doScan");
        isScanning = true;
        bluetoothAdapter.stopLeScan(callback);

        bluetoothAdapter.startLeScan(callback);
        if (sleepDuration > 0) {

            handle.sendEmptyMessageDelayed(0, scanDuration);
        }

    }




    public void stopScan() {
        isScanning = false;
        bluetoothAdapter.stopLeScan(callback);
        handle.removeMessages(0);
    }


    private BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        synchronized public void onLeScan(BluetoothDevice device, final int rssi, byte[] scanRecord) {
            BLEScanResult bleScanResult = new BLEScanResult(device, scanRecord,rssi);
            if (deviceScanCallBack != null) {
                deviceScanCallBack.onBLEDeviceScan(bleScanResult, rssi);
            }
        }
    };
    //***************************************************************************************//




}
