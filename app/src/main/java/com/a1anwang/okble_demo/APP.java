package com.a1anwang.okble_demo;

import android.app.Application;

import com.a1anwang.okble.client.core.OKBLEDevice;

/**
 * Created by a1anwang.com on 2018/5/16.
 */

public class APP extends Application{

    public OKBLEDevice okbleDevice;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
    }
}
