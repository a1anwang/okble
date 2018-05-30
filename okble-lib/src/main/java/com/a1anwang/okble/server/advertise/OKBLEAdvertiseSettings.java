package com.a1anwang.okble.server.advertise;

import android.bluetooth.le.AdvertiseSettings;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class OKBLEAdvertiseSettings {

    /**
     * Perform Bluetooth LE advertising in low power mode. This is the default and preferred
     * advertising mode as it consumes the least power.
     */
    public static final int OKBLE_ADVERTISE_MODE_LOW_POWER = AdvertiseSettings.ADVERTISE_MODE_LOW_POWER;

    /**
     * Perform Bluetooth LE advertising in balanced power mode. This is balanced between advertising
     * frequency and power consumption.
     */
    public static final int OKBLE_ADVERTISE_MODE_BALANCED = AdvertiseSettings.ADVERTISE_MODE_BALANCED;

    /**
     * Perform Bluetooth LE advertising in low latency, high power mode. This has the highest power
     * consumption and should not be used for continuous background advertising.
     */
    public static final int OKBLE_ADVERTISE_MODE_LOW_LATENCY = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY;



    private final boolean connectable;

    public boolean isConnectable() {
        return connectable;
    }

    private OKBLEAdvertiseSettings(boolean connectable){
        this.connectable=connectable;
    }

    public static final class Builder {
        private boolean mConnectable;

        public Builder setConnectable(boolean connectable) {
            mConnectable = connectable;
            return this;
        }

        public OKBLEAdvertiseSettings build() {
            return new OKBLEAdvertiseSettings(mConnectable);
        }

    }
}
