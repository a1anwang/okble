# okble: An easy-to-use BLE library for Android
[![](https://jitpack.io/v/a1anwang/okble.svg)](https://jitpack.io/#a1anwang/okble)
## [中文](https://github.com/a1anwang/okble/blob/master/README_zh.md) | English
## Summary
  BLE has two parts:Center & Peripheral. When we develop an android app,the most used is Center. Using Center-API to connect with Peripheral, and then communicate each other. For example, app can read battery level of a wristband, also app can control the bluetooth-light on/off.
### Center
  Center usually has three parts:Scan, Connect, Communicate. Communicate means swap data with Peripheral using method like Read, Write, Notify/Indicate. We use Read to get the data from Peripheral(for example, read battery level of a wristband). We use Write to send data to Peripheral(for example, send a turn-off command to turn the bluetooth-light off).  Notify/Indicate can help us receive the data which Peripheral uploaded(for example, the wristband uploads heart rate value every second, app can show a heart rate spectrum by receiving the value continuously). As we can see, it makes a bidirectional communication.

### Peripheral
  We could think that Peripheral is a hardware device, it works to provide data. We use little in developing android apps. In android5.0, google add some APIs about Peripheral so that we can make an android device to be a Peripheral. We can use two android phones to develop BLE app nicely. So easy! Mom doesn't have to worry about no Peripheral any more。Using Peripheral API, we can do a lot, for example chatting by BLE(there is a chatting demo using Classic Bluetooth in google samples), change an android device to an iBeacon.
### okble help us achieve the above functions easily
## Demo
[Download APK-Demo](https://github.com/a1anwang/okble/raw/master/app/build/outputs/apk/debug/app-debug.apk)

![](https://github.com/a1anwang/okble/blob/master/demo_qr.png)
  
  ![](https://github.com/a1anwang/okble/raw/master/demo.gif)

## Features
- Clear and easy to use, for all of BLE communications you just need an OKBLEDevice.
- OKBLE use a queue to manager BLE operation, you can read/write/notify/indicate every where and every time, you can diy operation overtime.
- Dynamic permissions included, there is a scan callback showing whether the location permission is granted  in target sdk 23 or above.
- minimum support API 18(android4.3)
- Double listeners，more smart: a global listener to monitor Peripheral status, a solely listener for each BLE operation.
- Auto reconnect: the auto-reconnect works continuously until connect successfully.
- Easy use for multi-Peripheral
- Support simulating a Peripheral, Support simulating an iBeacon.
- Support scanning iBeacon, Support Monitoring enter/exit iBeacon region.


## Usage
#### Add a gradle dependency
```
repositories {
  jcenter()
  maven { url "https://jitpack.io" }
}
dependencies {
  implementation 'com.github.a1anwang:okble:1.1.3'
}
```
#### Scan Peripheral
```
OKBLEScanManager scanManager=new OKBLEScanManager(this);
scanManager.setScanCallBack(scanCallBack);
DeviceScanCallBack scanCallBack=new DeviceScanCallBack() {
  @Override
  public void onBLEDeviceScan(BLEScanResult device, int rssi) {
      LogUtils.e(" scan:"+device.toString());
  }

  @Override
  public void onFailed(int code) {
    switch (code){
      case DeviceScanCallBack.SCAN_FAILED_BLE_NOT_SUPPORT:
      Toast.makeText(mContext,"the android deice do not support BLE",Toast.LENGTH_SHORT).show();
      break;
      case DeviceScanCallBack.SCAN_FAILED_BLUETOOTH_DISABLE:
      Toast.makeText(mContext,"please enable the bluetooth",Toast.LENGTH_SHORT).show();
      break;
      case DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE:
      Toast.makeText(mContext,"Location Authority has been rejected.",Toast.LENGTH_SHORT).show();
      break;
      case DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE_FOREVER:
      Toast.makeText(mContext,"Location Authority has been rejected forever.",Toast.LENGTH_SHORT).show();
      break;
    }
  }

  @Override
  public void onStartSuccess() {
  }
};
```
#### Connect to Peripher
```
OKBLEDevice okbleDevice=new OKBLEDeviceImp(mContext,bleScanResult);
//okbleDevice=new OKBLEDeviceImp(mContext);
//okbleDevice.setBluetoothDevice(mBluetoothDevice);
okbleDevice.addDeviceListener(this);
okbleDevice.connect(true);//true means OKBLE will auto reconnect to the Peripheral if the connection is disconnected.
```
#### APP disconnect forwardly
```
okbleDevice.disConnect(false); //false means don't need the auto-reconnect function provided by OKBLE after disconnect;after disconnect, you can use okbleDevice.connect() to reconnect.
```
#### APP clear the connection
```
okbleDevice.remove(); //remove will clear the connection completely; if you want to reconnect, make sure use setBleScanResult/setBluetoothDevice to set Peripheral before okbleDevice.connect();
```
#### Communicate
###### Read
```
okbleDevice.addReadOperation("feea", new BLEOperation.ReadOperationListener() {
  @Override
  public void onReadValue(final byte[] value) {
  runOnUiThread(new Runnable() {
    @Override
    public void run() {
        addLog("onReadValue:"+ OKBLEDataUtils.BytesToHexString(value)+" ("+new String(value)+")");
      }
    });
  }

  @Override
  public void onFail(int code, final String errMsg) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        addLog("read onFail:"+ errMsg);
      }
    });
  }

  @Override
  public void onExecuteSuccess(BLEOperation.OperationType type) {

  }
});
```
###### Write
```
okbleDevice.addWriteOperation("feea",value,new BLEOperation.WriteOperationListener() {
  @Override
  public void onWriteValue(final byte[] byteValue) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        addLog(" onWriteValue:"+value);
      }
    });
  }

  @Override
  public void onFail(int code, final String errMsg) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        addLog("write onFail:"+errMsg);
      }
    });
  }

  @Override
  public void onExecuteSuccess(BLEOperation.OperationType type) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        addLog("write value execute success");
      }
      });
    }
  });
```
###### Notify/Indicate
```
okbleDevice.addNotifyOrIndicateOperation("feea", true, new BLEOperation.NotifyOrIndicateOperationListener() {
  @Override
  public void onNotifyOrIndicateComplete() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        addLog("onNotifyOrIndicateComplete");
      }
    });
  }

  @Override
  public void onFail(int code, final String errMsg) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        addLog("NotifyOrIndicate onFail:"+ errMsg);
      }
    });
  }

  @Override
  public void onExecuteSuccess(BLEOperation.OperationType type) {
  }
});
```
##### Write big data(for example:OAD)
Exactly, OAD's principle is load byte[] from firmware file(like .bin), then send byte[] segmentations to Peripheral. The following code shows how to send byte[] segmentations, some details is hidden(like how to package each segmentation),because different products have different demands.
```
final int sendInterval=50;//interval of each package; you may set 80-100 for some android phones, beacuse too fast ,too higher failure rate;
okbleDevice.setOperationInterval(sendInterval);

byte[] oadValues=loadBytesFromFile(filePath);

final int blockSize=20;//one package contains 20 bytes
final int blockCount= (int) Math.ceil(oadValues.length*1.0f/blockSize);//the total package count
percent=0;
for (int i=0;i<blockCount;i++){
byte[] value=new byte[blockSize];
System.arraycopy(oadValues,i*blockSize,value,0,blockSize);
okbleDevice.addWriteOperation(OAD_WRITE_UUID, value, new OKBLEOperation.WriteOperationListener() {
  @Override
  public void onWriteValue(byte[] value) {
    percent++;
    float progress=percent*1.0f/blockCount;
    int leftSeconds= (int) ((sendInterval*blockCount)/1000*(1-progress));
    Log.e("TAG"," OAD progress:"+progress+" time left:"+leftSeconds +" seconds.");

  }

  @Override
  public void onFail(int code, String errMsg) {
    Log.e("TAG"," OAD failed");
    okbleDevice.clearOperations();
    break;
  }

  @Override
  public void onExecuteSuccess(OKBLEOperation.OperationType type) {

  }
 });
}
```
#### APP simulates a Peripheral (scanable and connectable)
```
OKBLEAdvertiseManager okbleAdvertiseManager;
OKBLEServerDevice serverDevice;

okbleAdvertiseManager=new OKBLEAdvertiseManager(this);
serverDevice=new OKBLEServerDeviceImp(this);

OKBLEAdvertiseSettings settings= new OKBLEAdvertiseSettings.Builder().setConnectable(true).build();
OKBLEAdvertiseData  data=new OKBLEAdvertiseData.Builder().setIncludeDeviceName(true).build();
//start advertising
okbleAdvertiseManager.startAdvertising(settings, data, new OKBLEAdvertiseCallback() {
    @Override
    public void onStartSuccess() {
      LogUtils.e("---onStartSuccess ---");
      Toast.makeText(mContext,"Advertising Success",Toast.LENGTH_SHORT).show();
      configServer();//config service 和characteristic
    }
    @Override
    public void onStartFailure(int errorCode, String errMsg) {
      LogUtils.e("---onStartFailure errMsg:"+errMsg);
      Toast.makeText(mContext,"Advertising Failed:"+errMsg,Toast.LENGTH_LONG).show();
    }
});
private void configServer(){
  OKBLEServiceModel serviceModel=new OKBLEServiceModel(CommonUUIDUtils.createCompleteUUIDByShortUUID("fff0"));

  OKBLECharacteristicModel characteristicModel=new OKBLECharacteristicModel(CommonUUIDUtils.createCompleteUUIDByShortUUID("fff1"));
  characteristicModel.setCanWrite(true);
  characteristicModel.setCanNotify(true);
  characteristicModel.setCanRead(true);

  OKBLECharacteristicModel characteristicModel_2=new  OKBLECharacteristicModel(CommonUUIDUtils.createCompleteUUIDByShortUUID("fff2"));
  characteristicModel_2.setCanWriteNoResponse(true);

  List<OKBLECharacteristicModel> characteristicModels=new ArrayList<>();
  characteristicModels.add(characteristicModel);
  characteristicModels.add(characteristicModel_2);

  serverDevice.addCharacteristic(characteristicModels, serviceModel, new OKBLEServerOperation.BLEServerOperationListener() {
      @Override
    public void onAddCharacteristicFailed(int errorCode, String errorMsg) {
      LogUtils.e("onAddCharacteristicFailed:"+errorMsg );
    }

    @Override
    public void onAddCharacteristicSuccess() {
      LogUtils.e("onAddCharacteristicSuccess");
    }
  });
}
```
#### Scan iBeacon
```
OKBLEBeaconScanManager scanManager;
scanManager=new OKBLEBeaconScanManager(this);
scanManager.setBeaconScanCallback(scanCallBack);
OKBLEBeaconScanManager.OKBLEBeaconScanCallback scanCallBack=new OKBLEBeaconScanManager.OKBLEBeaconScanCallback() {
    @Override
    public void onScanBeacon(OKBLEBeacon beacon) {
      Log.e("TAG"," scan beacon:"+beacon.toString());
    }
};
```
#### APP simulates an iBeacon
```
OKBLEBeBeaconManager beBeaconManager;
beBeaconManager=new OKBLEBeBeaconManager(this);
beBeaconManager.setOKBLEBeBeaconListener(startBeaconListener);

String uuid ="12345678-1234-1234-1234-1234567890ab";
int major=1;
int minor=2;
beBeaconManager.startIBeacon(uuid,major,minor);

OKBLEBeBeaconManager.OKBLEStartBeaconListener startBeaconListener=new OKBLEBeBeaconManager.OKBLEStartBeaconListener() {
  @Override
  public void onStartSuccess() {
    Toast.makeText(mContext,"start success",Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onStartFailure(String errMsg) {
    Toast.makeText(mContext,"start failed:"+errMsg,Toast.LENGTH_SHORT).show();
  }
};
```
#### Monitor enter/exit iBeacon region
```
OKBLEBeaconManager beaconManager=new OKBLEBeaconManager(this);
beaconManager.setRegionListener(this);
String uuid ="12345678-1234-1234-1234-1234567890ab";
int major=1;
int minor=2;
OKBLEBeaconRegion okbleBeaconRegion=OKBLEBeaconRegion.getInstance(uuid,major,minor);
//OKBLEBeaconRegion okbleBeaconRegion=OKBLEBeaconRegion.getInstance(uuid,major);
//OKBLEBeaconRegion okbleBeaconRegion=OKBLEBeaconRegion.getInstance(uuid);
beaconManager.startMonitoringForRegion(okbleBeaconRegion);

@Override
public void onEnterBeaconRegion(OKBLEBeaconRegion beaconRegion) {

}

@Override
public void onExitBeaconRegion(OKBLEBeaconRegion beaconRegion) {

}
```


## Discuss

Contact me: admin@a1anwang.com

License
-------

Copyright 2018 a1anwang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
