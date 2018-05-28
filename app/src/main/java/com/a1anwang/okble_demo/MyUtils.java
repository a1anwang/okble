package com.a1anwang.okble_demo;

/**
 * Created by a1anwang.com on 2018/5/24.
 */

public class MyUtils {
    private static long lastClickTime=0;

    private static long clickInterval=300;//点击间隔

    public static boolean isFastClick(){
        long current= System.currentTimeMillis();
        if((current-lastClickTime)>clickInterval){
            lastClickTime=current;
            return false;
        }
        return true;
    }
}
