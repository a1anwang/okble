package com.a1anwang.okble.common;

import android.util.Log;

public class LogUtils {

	
	public static boolean open=true;
	
	
	public static void e(String TAG,String msg){
		if(open)
		Log.e(TAG, msg);
	}

	public static void e(String msg){
		if(open)
			Log.e("OKBLE", msg);
	}
}
