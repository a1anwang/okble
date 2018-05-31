package com.a1anwang.okble.server.advertise;

import android.os.ParcelUuid;
import android.support.v4.util.ArrayMap;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class OKBLEAdvertiseData {



    private List<ParcelUuid> mServiceUuids = new ArrayList<ParcelUuid>();
    private SparseArray<byte[]> mManufacturerSpecificData = new SparseArray<byte[]>();
    private Map<ParcelUuid, byte[]> mServiceData = new ArrayMap<ParcelUuid, byte[]>();
    private boolean mIncludeTxPowerLevel;
    private boolean mIncludeDeviceName;

    private OKBLEAdvertiseData(List<ParcelUuid> serviceUuids, SparseArray<byte[]> manufacturerData, Map<ParcelUuid, byte[]> serviceData, boolean includeTxPowerLevel, boolean includeDeviceName) {
        mServiceUuids = serviceUuids;
        mManufacturerSpecificData = manufacturerData;
        mServiceData = serviceData;
        mIncludeTxPowerLevel = includeTxPowerLevel;
        mIncludeDeviceName = includeDeviceName;
    }

    public boolean getIncludeDeviceName() {
        return mIncludeDeviceName;
    }

    public boolean getIncludeTxPowerLevel() {
        return mIncludeTxPowerLevel;
    }

    public List<ParcelUuid> getServiceUuids() {
        return mServiceUuids;
    }

    public SparseArray<byte[]> getManufacturerSpecificData() {
        return mManufacturerSpecificData;
    }

    public Map<ParcelUuid, byte[]> getServiceData() {
        return mServiceData;
    }

    public static final class Builder {
        private List<ParcelUuid> mServiceUuids = new ArrayList<ParcelUuid>();
        private SparseArray<byte[]> mManufacturerSpecificData = new SparseArray<byte[]>();
        private Map<ParcelUuid, byte[]> mServiceData = new  ArrayMap<ParcelUuid, byte[]>();
        private boolean mIncludeTxPowerLevel;
        private boolean mIncludeDeviceName;


        public Builder addServiceUuid(ParcelUuid serviceUuid) {
            if (serviceUuid == null) {
                throw new IllegalArgumentException("serivceUuids are null");
            }
            mServiceUuids.add(serviceUuid);
            return this;
        }

        public Builder addServiceData(ParcelUuid serviceDataUuid, byte[] serviceData) {
            if (serviceDataUuid == null || serviceData == null) {
                throw new IllegalArgumentException(
                        "serviceDataUuid or serviceDataUuid is null");
            }
            mServiceData.put(serviceDataUuid, serviceData);
            return this;
        }

        public Builder addManufacturerData(int manufacturerId, byte[] manufacturerSpecificData) {
            if (manufacturerId < 0||manufacturerId>0xff) {
                throw new IllegalArgumentException(
                        "invalid manufacturerId - " + manufacturerId +" valid range:[0,0xFF]");
            }
            if (manufacturerSpecificData == null) {
                throw new IllegalArgumentException("manufacturerSpecificData is null");
            }
            mManufacturerSpecificData.put(manufacturerId, manufacturerSpecificData);
            return this;
        }
//        /**
//         * Whether the transmission power level should be included in the advertise packet. Tx power
//         * level field takes 3 bytes in advertise packet.
//         */
//        public Builder setIncludeTxPowerLevel(boolean includeTxPowerLevel) {
//            mIncludeTxPowerLevel = includeTxPowerLevel;
//            return this;
//        }

        /**
         * Set whether the device name should be included in advertise packet.
         */
        public Builder  setIncludeDeviceName(boolean includeDeviceName) {
            mIncludeDeviceName = includeDeviceName;
            return this;
        }


        public OKBLEAdvertiseData build() {
            return new OKBLEAdvertiseData(mServiceUuids, mManufacturerSpecificData, mServiceData,
                    mIncludeTxPowerLevel, mIncludeDeviceName);
        }
    }
}
