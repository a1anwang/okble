package com.a1anwang.okble.beacon;

import android.bluetooth.BluetoothDevice;

/**
 * Created by a1anwang.com on 2018/6/1.
 */

public class OKBLEBeacon {
    private String name;
    private int major;
    private int minor;
    private String uuid;
    private BluetoothDevice device;
    private int measuredPower;

    private int rssi;

    public void setMeasuredPower(int measuredPower) {
        this.measuredPower = measuredPower;
    }

    public int getMeasuredPower() {
        return measuredPower;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {

        return "Beacon:[ uuid:"+uuid+" major:"+major+" minor:"+minor+"]";
    }

    public String getIdentifier(){
        String key= uuid+"_"+major+"_"+minor;
        return key;
    }
}
