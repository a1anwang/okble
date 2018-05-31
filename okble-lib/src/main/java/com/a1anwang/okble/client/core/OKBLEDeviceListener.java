package com.a1anwang.okble.client.core;

/**
 * Created by a1anwang.com on 2017/6/27.
 * contact：www.a1anwang.com
 */

public interface OKBLEDeviceListener {

    public void onConnected(String deviceTAG);

    public void onDisconnected(String deviceTAG);
    public void onReadBattery(String deviceTAG, int battery);

    public void onReceivedValue(String deviceTAG,String uuid,byte[] value);

    public void onWriteValue(String deviceTAG,String uuid,byte[] value,boolean success);

    public void onReadValue(String deviceTAG,String uuid,byte[] value,boolean success);

    public void onNotifyOrIndicateComplete(String deviceTAG,String uuid,boolean enable,boolean success);
}
