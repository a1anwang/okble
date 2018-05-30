package com.a1anwang.okble.server.core;

import com.a1anwang.okble.common.OKBLECharacteristicModel;
import com.a1anwang.okble.common.OKBLEServiceModel;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class OKBLEServerOperation {
    public OKBLECharacteristicModel characteristicModel;
    public OKBLEServiceModel serviceModel;



    public interface BLEServerOperationListener{
        void onAddCharacteristicFailed(int errorCode,String errorMsg);
        void onAddCharacteristicSuccess();
    }
}
