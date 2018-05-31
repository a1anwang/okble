package com.a1anwang.okble.client.core;

import android.bluetooth.BluetoothGatt;
import android.util.SparseArray;

/**
 * Created by a1anwang.com on 2018/5/24.
 */

public class OKBLEOperationFailedDescUtils {

    private static SparseArray<String> descArray=new SparseArray<>();

    static {
        descArray.put(BluetoothGatt.GATT_READ_NOT_PERMITTED,"GATT read operation is not permitted");
        descArray.put(BluetoothGatt.GATT_WRITE_NOT_PERMITTED,"GATT write operation is not permitted");
        descArray.put(BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION,"Insufficient authentication for a given operation");
        descArray.put(BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED,"The given request is not supported");
        descArray.put(BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION,"Insufficient encryption for a given operation");
        descArray.put(BluetoothGatt.GATT_INVALID_OFFSET,"A read or write operation was requested with an invalid offset");
        descArray.put(BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH,"A write operation exceeds the maximum length of the attribute");
        descArray.put(BluetoothGatt.GATT_CONNECTION_CONGESTED,"A remote device connection is congested");
        descArray.put(BluetoothGatt.GATT_FAILURE,"A GATT operation failed");
        descArray.put(0x0b,"value length is more than mtu size");
    }
    public static String getDesc(int errCode){
        return descArray.get(errCode,"unknown error");
    }
}
