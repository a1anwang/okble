package com.a1anwang.okble.server.advertise;

import android.util.SparseArray;

/**
 * Created by a1anwang.com on 2018/5/24.
 */

public class OKBLEAdvertiseFailedDescUtils {

    private static SparseArray<String> descArray=new SparseArray<>();

    static {
        descArray.put(OKBLEAdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE,"Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
        descArray.put(OKBLEAdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS,"Failed to start advertising because no advertising instance is available.");
        descArray.put(OKBLEAdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED,"Failed to start advertising as the advertising is already started.");
        descArray.put(OKBLEAdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR,"Operation failed due to an internal error.");
        descArray.put(OKBLEAdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED,"This feature is not supported on this platform.");
        descArray.put(OKBLEAdvertiseCallback.ADVERTISE_FAILED_NULL_ADVERTISER,"This advertiser is null.");


    }
    public static String getDesc(int errCode){
        return descArray.get(errCode,"unknown error");
    }
}
