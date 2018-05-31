package com.a1anwang.okble.client.core;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by a1anwang.com on 2018/5/16.
 */

public class OKBLEOperation {
    public byte[] value;
    public int mtu;
    public BluetoothGattCharacteristic bleChar;

    public OKBLEOperation.OperationType operationType;

    public BaseOperationListener operationListener;


    public enum OperationType {
        OperationType_Read, OperationType_Write,OperationType_Write_No_Response,OperationType_Enable_Notify,OperationType_Enable_Indicate,OperationType_Disable_Notify,OperationType_Disable_Indicate,OperationType_Change_MTU
    }


    public interface BaseOperationListener{
        /**
         * 设备未连接
         */
        public static final int Operation_FAILED_Device_Not_Connected=1;

        /**
         * 数据为空
         */
        public static final int Operation_FAILED_Null_Value=2;


        /**
         * UUID不合法
         */
        public static final int Operation_FAILED_Invalid_UUID=3;


        /**
         * 没有此特征值
         */
        public static final int Operation_FAILED_Characteristic_Not_Found=4;

        /**
         * 没有此功能
         */
        public static final int Operation_FAILED_Characteristic_Property_Not_Found=5;

        /**
         * 操作超时
         */
        public static final int Operation_FAILED_Overtime=6;

        /**
         * BLE内部操作返回失败
         */
        public static final int Operation_FAILED_BLE_Failed=7;

        /**
         * 其他错误
         */
        public static final int Operation_FAILED_Other=8;

        void onFail(int code,String errMsg);

        /**
         * 表示执行成功(比如低于write来说,仅仅表示write这个动作发出去了,成不成功还要看onFail/onWriteValue)
         * @param type
         */
        void onExecuteSuccess(OperationType type);
    }


    public interface ReadOperationListener extends  BaseOperationListener{
        void onReadValue(byte[] value);
    }

    public interface WriteOperationListener extends  BaseOperationListener{
        void onWriteValue(byte[] value);
    }


    public interface NotifyOrIndicateOperationListener extends  BaseOperationListener{
        void onNotifyOrIndicateComplete();
    }
    public interface ChangeMTUListener extends  BaseOperationListener{
        void onMtuChange(int mtu);
    }
}
