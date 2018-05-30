package com.a1anwang.okble.client.scan;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.a1anwang.okble.common.OKBLEDataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a1anwang.com on 2017/6/26.
 * contact：www.a1anwang.com
 */

public class BLEScanResult implements Parcelable {

    private String TAG = "BLEScanResult";

    // The following data type values are assigned by Bluetooth SIG.
    // For more details refer to Bluetooth 4.1 specification, Volume 3, Part C, Section 18.
    private static final int DATA_TYPE_FLAGS = 0x01;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 0x02;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 0x03;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 0x04;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 0x05;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 0x06;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 0x07;
    private static final int DATA_TYPE_LOCAL_NAME_SHORT = 0x08;
    private static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 0x09;
    private static final int DATA_TYPE_TX_POWER_LEVEL = 0x0A;
    private static final int DATA_TYPE_SERVICE_DATA = 0x16;
    private static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;


    private BluetoothDevice bluetoothDevice;

    private byte[] advertisingData;
    private int rssi;
    private int txPowerLevel = Integer.MIN_VALUE;
    private List<String> serviceUuids;

    private SparseArray<byte[]> manufacturerSpecificData;

    private String completeLocalName;

    private Map<String, byte[]> serviceData;


    protected BLEScanResult(Parcel in) {
        TAG = in.readString();
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        advertisingData = in.createByteArray();
        rssi = in.readInt();
        txPowerLevel=in.readInt();
        serviceUuids = in.createStringArrayList();
        completeLocalName = in.readString();
    }

    public static final Creator<BLEScanResult> CREATOR = new Creator<BLEScanResult>() {
        @Override
        public BLEScanResult createFromParcel(Parcel in) {
            return new BLEScanResult(in);
        }

        @Override
        public BLEScanResult[] newArray(int size) {
            return new BLEScanResult[size];
        }
    };

    public String getMacAddress() {
        if (bluetoothDevice != null) {
            return bluetoothDevice.getAddress();
        }
        return null;
    }

    public String getCompleteLocalName() {
        return completeLocalName;
    }

    public int getRssi() {
        return rssi;
    }
// result:ScanRecord[mAdvertiseFlags=5, mServiceUuids=[00001803-0000-1000-8000-00805f9b34fb, 00001802-0000-1000-8000-00805f9b34fb, 00001804-0000-1000-8000-00805f9b34fb, 00001805-0000-1000-8000-00805f9b34fb],
    // mManufacturerSpecificData={60540=[121, -40, -115, -101]}, mServiceData={}, mTxPowerLevel=-2147483648, mDeviceName=SmartKeePro]
    //0x020105 09 02 0318 0218 0418 0518 0C09 536D6172744B656550726F  07 FF 987BF3610CFD

    //0x020105 09 02 0318 0218 0418 0518 0C09 536D6172744B656550726F  07 FF 7CEC79D89077

    public BLEScanResult(BluetoothDevice bluetoothDevice, byte[] advertisingData, int rssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.advertisingData = advertisingData;

        this.rssi = rssi;
      //  LogUtils.e(TAG, " rssi:" + rssi + " mac:" + bluetoothDevice.getAddress() + " name:" + bluetoothDevice.getName());
      //  LogUtils.e(TAG, " advertisingData:" + OKBLEDataUtils.Bytes2HexString(advertisingData));
        analyzeAdvertisingData();

    }

    public byte[] getAdvertisingData() {
        return advertisingData;
    }

    public int getTxPowerLevel() {
        return txPowerLevel;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public List<String> getServiceUuids() {
        if (serviceUuids == null) {
            analyzeAdvertisingData();
        }
        return serviceUuids;
    }

    public SparseArray<byte[]> getManufacturerSpecificData() {
        if (manufacturerSpecificData == null) {
            analyzeAdvertisingData();
        }
        return manufacturerSpecificData;
    }

    public Map<String, byte[]> getServiceData() {
        if (serviceData == null) {
            analyzeAdvertisingData();
        }
        return serviceData;
    }


    synchronized private void analyzeAdvertisingData() {

        if (advertisingData != null && advertisingData.length > 0) {
            int index = 0;

            while (index < advertisingData.length - 1) {
                int length = advertisingData[index];
                if (length == 0) {
                    break;
                }
                int type = advertisingData[index + 1];
                //LogUtils.e(TAG, " type:" + OKBLEDataUtils.Bytes2HexString(new byte[]{(byte) type}));
                if (type == (byte) DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL) {
                    serviceUuids = new ArrayList<>();
                    byte[] serveruuids = OKBLEDataUtils.subByteArray(advertisingData, index + 2, length - 1);
                   // LogUtils.e(TAG, " serveruuids:" + OKBLEDataUtils.Bytes2HexString(serveruuids));

                    if (serveruuids.length % 2 == 0) {

                        int count = serveruuids.length / 2;
                        for (int i = 0; i < count; i++) {
                            byte[] serveruuid = new byte[]{serveruuids[2 * i + 1], serveruuids[2 * i]};
                            //String uuid = CommonUUIDUtils.CommonUUIDStr_x.replace("xxxx", OKBLEDataUtils.BytesToHexString(serveruuid).toLowerCase());
                            String uuid=OKBLEDataUtils.BytesToHexString(serveruuid);
                            serviceUuids.add(uuid);
                     //       LogUtils.e(TAG, "    serveruuid:" + uuid);
                        }


                    }
                }else if(type == (byte) DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE){
                    serviceUuids = new ArrayList<>();
                    byte[] serveruuids = OKBLEDataUtils.subByteArray(advertisingData, index + 2, length - 1);
                    // LogUtils.e(TAG, " serveruuids:" + OKBLEDataUtils.Bytes2HexString(serveruuids));

                    if (serveruuids.length % 2 == 0) {

                        int count = serveruuids.length / 2;
                        for (int i = 0; i < count; i++) {
                            byte[] serveruuid = new byte[]{serveruuids[2 * i + 1], serveruuids[2 * i]};
                            //String uuid = CommonUUIDUtils.CommonUUIDStr_x.replace("xxxx", OKBLEDataUtils.BytesToHexString(serveruuid).toLowerCase());
                            String uuid=OKBLEDataUtils.BytesToHexString(serveruuid);
                            serviceUuids.add(uuid);
                            //       LogUtils.e(TAG, "    serveruuid:" + uuid);
                        }


                    }
                }else if (type == (byte) DATA_TYPE_SERVICE_DATA) {
                    serviceData = new HashMap<>();
                    byte[] serverData = OKBLEDataUtils.subByteArray(advertisingData, index + 2, length - 1);
                    byte[] uuid = new byte[]{serverData[1], serverData[0]};
                    byte[] data = OKBLEDataUtils.subByteArray(serverData, 2, serverData.length - 2);
                   // String uuidStr = CommonUUIDUtils.CommonUUIDStr_x.replace("xxxx", OKBLEDataUtils.BytesToHexString(uuid).toLowerCase());
                    String uuidStr=OKBLEDataUtils.BytesToHexString(uuid);
                    serviceData.put(uuidStr, data);

                 //   LogUtils.e(TAG, "    server data :" + OKBLEDataUtils.Bytes2HexString(data) + " uuid:" + uuidStr);

                } else if (type == (byte) DATA_TYPE_MANUFACTURER_SPECIFIC_DATA) {
                    manufacturerSpecificData = new SparseArray<>();
                    byte[] manufacturerData = OKBLEDataUtils.subByteArray(advertisingData, index + 2, length - 1);
                    byte[] manufacturerId = new byte[]{manufacturerData[1], manufacturerData[0]};
                //    LogUtils.e(TAG, " manufacturerId:" + OKBLEDataUtils.Bytes2HexString(manufacturerId));
                    byte[] manufacturerValue = OKBLEDataUtils.subByteArray(manufacturerData, 2, manufacturerData.length - 2);
                    manufacturerSpecificData.append(OKBLEDataUtils.buildUint16(manufacturerId[0], manufacturerId[1]), manufacturerValue);
                 //   LogUtils.e(TAG, " manufacturerValue:" + OKBLEDataUtils.Bytes2HexString(manufacturerValue));


                } else if (type == (byte) DATA_TYPE_LOCAL_NAME_COMPLETE) {

                    byte[] nameData = OKBLEDataUtils.subByteArray(advertisingData, index + 2, length - 1);
                    completeLocalName = new String(nameData);
                 //   LogUtils.e(TAG, " completeLocalName:" + completeLocalName);
                }else if (type == (byte) DATA_TYPE_TX_POWER_LEVEL) {

                    txPowerLevel = advertisingData[index + 2];
                }


                index += length + 1;//1是length自身占一个字节

            }


        }
    }

    @Override
    public String toString() {
        return "[ScanResult:"+" mac:"+getMacAddress()+" name:"+getBluetoothDevice().getName()+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(TAG);
        dest.writeParcelable(bluetoothDevice, flags);
        dest.writeByteArray(advertisingData);
        dest.writeInt(rssi);
        dest.writeInt(txPowerLevel);
        dest.writeStringList(serviceUuids);
        dest.writeString(completeLocalName);
    }

}
