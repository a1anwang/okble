package com.a1anwang.okble.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by a1anwang.com on 2018/5/22.
 */

public class OKBLECharacteristicModel implements Parcelable{
    private String uuid;

    private boolean canRead;
    private boolean canWrite;
    private boolean canWriteNoResponse;
    private boolean canNotify;
    private boolean canIndicate;

    public OKBLECharacteristicModel(String uuid){
        this.uuid=uuid;
    }

    protected OKBLECharacteristicModel(Parcel in) {
        uuid = in.readString();
        canRead = in.readByte() != 0;
        canWrite = in.readByte() != 0;
        canWriteNoResponse = in.readByte() != 0;
        canNotify = in.readByte() != 0;
        canIndicate = in.readByte() != 0;
        desc = in.readString();
    }

    public static final Creator<OKBLECharacteristicModel> CREATOR = new Creator<OKBLECharacteristicModel>() {
        @Override
        public OKBLECharacteristicModel createFromParcel(Parcel in) {
            return new OKBLECharacteristicModel(in);
        }

        @Override
        public OKBLECharacteristicModel[] newArray(int size) {
            return new OKBLECharacteristicModel[size];
        }
    };

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    private String desc;

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setCanIndicate(boolean canIndicate) {
        this.canIndicate = canIndicate;
    }

    public void setCanNotify(boolean canNotify) {
        this.canNotify = canNotify;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    public void setCanWriteNoResponse(boolean canWriteNoResponse) {
        this.canWriteNoResponse = canWriteNoResponse;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public boolean isCanIndicate() {
        return canIndicate;
    }

    public boolean isCanNotify() {
        return canNotify;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public boolean isCanWriteNoResponse() {
        return canWriteNoResponse;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeByte((byte) (canRead ? 1 : 0));
        dest.writeByte((byte) (canWrite ? 1 : 0));
        dest.writeByte((byte) (canWriteNoResponse ? 1 : 0));
        dest.writeByte((byte) (canNotify ? 1 : 0));
        dest.writeByte((byte) (canIndicate ? 1 : 0));
        dest.writeString(desc);
    }
}
