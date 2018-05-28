package com.a1anwang.okble.client.scan;

/**
 * Created by a1anwang.com on 2017/6/26.
 * contact：www.a1anwang.com
 */

public interface DeviceScanCallBack {
    public static final int SCAN_FAILED_BLUETOOTH_DISABLE=1;
    public static final int SCAN_FAILED_BLE_NOT_SUPPORT=2;
    public static final int SCAN_FAILED_LOCATION_PERMISSION_DISABLE=3;
    public static final int SCAN_FAILED_LOCATION_PERMISSION_DISABLE_FOREVER=4;


    public void onBLEDeviceScan(BLEScanResult device, final int rssi);
    public void onFailed(int code);
    public void onStartSuccess();
}
