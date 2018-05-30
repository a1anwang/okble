package com.a1anwang.okble.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a1anwang.com on 2018/5/22.
 */

public class OKBLEServiceModel {

    private String uuid;

    private String desc;

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    private List<OKBLECharacteristicModel> characteristicModels;


    public OKBLEServiceModel(String uuid){
        this.uuid = uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void addCharacteristicModel(OKBLECharacteristicModel characteristicModel) {
        if(characteristicModels==null){
            characteristicModels=new ArrayList<>();
        }
        characteristicModels.add(characteristicModel);
    }

    public List<OKBLECharacteristicModel> getCharacteristicModels() {
        return characteristicModels;
    }
}
