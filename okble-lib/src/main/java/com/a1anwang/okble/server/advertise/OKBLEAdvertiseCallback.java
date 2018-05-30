package com.a1anwang.okble.server.advertise;

import android.bluetooth.le.AdvertiseCallback;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public interface OKBLEAdvertiseCallback {
    /**
     * Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.
     */
    public static final int ADVERTISE_FAILED_DATA_TOO_LARGE = AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE;

    /**
     * Failed to start advertising because no advertising instance is available.
     */
    public static final int ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS;

    /**
     * Failed to start advertising as the advertising is already started.
     */
    public static final int ADVERTISE_FAILED_ALREADY_STARTED = AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED;

    /**
     * Operation failed due to an internal error.
     */
    public static final int ADVERTISE_FAILED_INTERNAL_ERROR = AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR;

    /**
     * This feature is not supported on this platform.
     */
    public static final int ADVERTISE_FAILED_FEATURE_UNSUPPORTED = AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED;


    public static final int ADVERTISE_FAILED_NULL_ADVERTISER =0x10;


    void onStartSuccess();
    /**
     * Callback when advertising could not be started.
     *
     * @param errorCode Error code (see ADVERTISE_FAILED_* constants) for advertising start
     *            failures.
     */
    void onStartFailure(int errorCode,String errMsg);
}
