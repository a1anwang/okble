package com.a1anwang.okble.beacon;

/**
 * Created by a1anwang.com on 2018/7/20.
 */

public class OKBLEBeaconRegion {
    private int major=-1;
    private int minor=-1;
    private String uuid;


    @Override
    public String toString() {

        return "BeaconRegion:[uuid:"+uuid+" major:"+major+" minor:"+minor+"]";
    }

    public static OKBLEBeaconRegion getInstance(String uuid){
        return new OKBLEBeaconRegion(uuid);
    }


    public static OKBLEBeaconRegion getInstance(String uuid, int major){
        return new OKBLEBeaconRegion(uuid,major);
    }


    public static OKBLEBeaconRegion getInstance(String uuid ,int major,int minor){
        return new OKBLEBeaconRegion(uuid,major,minor);
    }


    private OKBLEBeaconRegion(String uuid) {
        super();
        this.uuid = uuid;
    }

    private OKBLEBeaconRegion(String uuid, int major) {
        super();

        this.uuid = uuid;
        this.major=major;
    }
    private OKBLEBeaconRegion(String uuid ,int major,int minor){
        super();
        this.major=major;
        this.minor=minor;
        this.uuid=uuid;
    }

    public String getIdentifier(){
        return this.uuid+"_"+this.major+"_"+this.minor;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }
}