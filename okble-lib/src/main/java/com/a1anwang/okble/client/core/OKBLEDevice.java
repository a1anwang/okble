package com.a1anwang.okble.client.core;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import com.a1anwang.okble.client.scan.BLEScanResult;
import com.a1anwang.okble.common.OKBLEServiceModel;

import java.util.List;

/**
 * Created by a1anwang.com on 2018/5/16.
 */

public interface OKBLEDevice {
    public enum DeviceStatus{
        DEVICE_STATUS_INITIAL,DEVICE_STATUS_CONNECTING,DEVICE_STATUS_CONNECTED,DEVICE_STATUS_DISCONNECTED
    }
    public static final int Default_OperationOverTime=3*1000;
    /**
     * 设置TAG, 此TAG为多设备场景下分辨设备提供一种实现;如果不设置,deviceTAG会默认为设置的蓝牙设备的MAC地址
     * @param deviceTag
     */
    void setDeviceTAG(String deviceTag);

    /**
     * 获取TAG
     */
    String getDeviceTAG();

    BluetoothDevice getBluetoothDevice();

    void addDeviceListener(OKBLEDeviceListener OKBLEDeviceListener);
    void removeDeviceListener(OKBLEDeviceListener OKBLEDeviceListener);
    void clearDeviceListener();
    /**
     * 设置蓝牙操作(读,写,打开/关闭通知/指示)的超时时间,单位毫秒
     * @param overTime
     */
    void setOperationOverTime(int overTime);

    /**
     * 设置蓝牙操作(读,写,打开/关闭通知/指示)的间隔时间,单位毫秒,默认30毫秒
     * @param interval
     */
    void setOperationInterval(int interval);

    DeviceStatus getDeviceStatus();


    /**
     * 开始连接
     * @param autoReconnect 是否需要在连接断开的时候自动从立案.
     * @return true 只表示连接方法执行成功
     */
    boolean connect(boolean autoReconnect);

    /**
     * 设置要连接的蓝牙设备
     * @param scanResult
     */
    void setBleScanResult(BLEScanResult scanResult);

    /**
     * 设置要连接的蓝牙设备
     * @param bluetoothDevice
     */
    void setBluetoothDevice(BluetoothDevice bluetoothDevice);

    /**
     * 主动断开连接
     * @param autoReconnect 是否需要在连接断开的时候自动重连. autoReconnect 为true的使用场景:需要手机主动断开一次,然后再次重新连接,比如需要进行某些大型数据传输前,设备需要先断开重新初始化.
     * @return true 只表示连接方法执行成功
     */
    boolean disConnect(boolean autoReconnect);

    /**
     * 清除连接(包括连接的设备信息),清除所有操作,再次连接需要调用 {@link OKBLEDevice#setBleScanResult}或 {@link OKBLEDevice#setBluetoothDevice} 设置目标设备
     */
    void remove();

    /**添加写入数据的操作
     * @param characteristicUUID 长度须为4，比如 ffe1;或者是一个完整的uuid:0000ffe1-0000-1000-8000-00805f9b34fb
     * @param value    16进制字符串形式的数据,如 EB16
     */
    void addWriteOperation(String characteristicUUID, String value, OKBLEOperation.WriteOperationListener listener);


    /**添加写入数据的操作
     * @param characteristicUUID 长度须为4，比如 ffe1;或者是一个完整的uuid:0000ffe1-0000-1000-8000-00805f9b34fb
     * @param value    byte[]形式的数据
     */
    void addWriteOperation(String characteristicUUID, byte[] value,OKBLEOperation.WriteOperationListener listener);

    /**
     *添加读特征值的操作
     * @param characteristicUUID 长度须为4，比如 ffe1;或者是一个完整的uuid:0000ffe1-0000-1000-8000-00805f9b34fb
     */
    void addReadOperation(String characteristicUUID,OKBLEOperation.ReadOperationListener listener);

    /**
     * 添加开启/关闭 通知/指示的操作,会自动识别是通知还是指示
     * @param characteristicUUID 长度须为4，比如 ffe1;或者是一个完整的uuid:0000ffe1-0000-1000-8000-00805f9b34fb
     * @param enable   是否开启
     */
    void addNotifyOrIndicateOperation(String characteristicUUID,boolean enable,OKBLEOperation.NotifyOrIndicateOperationListener listener);

    void clearOperations();

    /**
     *配置mtu
     * @param mtu
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void addChangeMTUOperation(int mtu,OKBLEOperation.ChangeMTUListener changeMTUListener);

    boolean isNotifyEnabled(String uuid);
    boolean isIndicateEnabled(String uuid);
    /**
     *
     * @return 获取设备的服务和特征值
     */
    List<OKBLEServiceModel> getServiceModels();

}
