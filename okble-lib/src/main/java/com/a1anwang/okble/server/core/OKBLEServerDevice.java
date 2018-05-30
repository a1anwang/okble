package com.a1anwang.okble.server.core;

import com.a1anwang.okble.common.OKBLECharacteristicModel;
import com.a1anwang.okble.common.OKBLEServiceModel;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public interface OKBLEServerDevice {


    void addCharacteristic(OKBLECharacteristicModel okbleCharacteristicModel, OKBLEServiceModel okbleServiceModel);


}
