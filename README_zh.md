[![](https://jitpack.io/v/a1anwang/okble.svg)](https://jitpack.io/#a1anwang/okble)
# okble: 简单易用的 Android BLE library
## 简介
  BLE有Center(简单理解为客户端)和Peripheral(服务端)2个角色。我们android开发常用的就是客户端角色，使用API连接硬件外设(服务端)，然后进行相关的数据交互，如读取手环上的心率电量等数据，控制蓝牙灯的开关。
### Center
  Center主要可以分为 扫描->连接->通讯 三块内容. 通讯主要包含read，write，notify/indicate。read就是读取设备上的数据(如读取外设电量)，write就是发送数据(如发送关闭命令关闭蓝牙灯)，notify/indicate 字面意思就是通知/指示, 是用来接收设备主动上报的数据的(如手环可以每隔1秒就告诉APP心率值, 然后APP展现一个心率谱图)，这样就达成了双向通讯。
### Peripheral
  Peripheral主要理解为硬件外设，提供数据用的。在开发APP时很少关心这个，因为大家都是直接拿着硬件来调试的。在android5.0时，增加了Peripheral相关的API， 意味着可以让android设备模拟成外设，作为Peripheral来提供数据。这样，当我们没有硬件设备的时候，可以拿2台手机进行BLE开发，很方便。还可以扩展很多其他功能，比如使用BLE实现蓝牙聊天(google sample里面有经典蓝牙的聊天demo)，还可以把手机模拟成iBeacon等等。
### okble将会让我们轻松实现上面提到的功能
## Demo
[下载 APK-Demo](https://github.com/a1anwang/okble/raw/master/app/build/outputs/apk/debug/app-debug.apk)

![](https://github.com/a1anwang/okble/blob/master/demo_qr.png)
  
  ![](https://github.com/a1anwang/okble/raw/master/demo.gif)
## 功能特点:
 - 简单明了，一个OKBLEDevice即可完成所有通讯操作
 - 顺序执行通讯操作，可以在任何地方任何时机调用read/write/notify/indicate等通讯方法，不用担心命令丢失、命令不执行。BLE内部是不允许同时执行多次通讯的，okble使用队列让操作排队，先进先执行，保证所有的操作都能执行，可自定义操作超时时间
 - 动态权限集成，target sdk在23(android6.0)及以上时, 扫描是需要定位权限的，okble已集成，会有回调告知是否已授权
 - 向下兼容到API 18(android4.3)，在android5.0中google变更了扫描api，okble依然使用的是4.3的api，只为了兼容更多的手机
 - 双回调，处理灵活:定义了统一的listener来监听设备的状态，也可以为每一个蓝牙通讯操作设置单独的监听，方便页面多的情况下使用
 - 自动重连:断开后可以实现自动连接，直到连接成功
 - 自动识别write type，自动识别notify or indicate
 - 多设备连接管理简单
 - 支持APP模拟成外设，支持APP模拟成iBeacon
 - 支持扫描识别iBeacon，支持iBeacon区域的进入和退出检测
 
 
 ## 立即开始
  #### 添加依赖
 ```
   repositories {
        jcenter()
        maven { url "https://jitpack.io" }
   }
   dependencies {
         implementation 'com.github.a1anwang:okble:1.1.3'
   }
 ```
  #### 扫描外设
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
                    Toast.makeText(mContext,"该设备不支持BLE",Toast.LENGTH_SHORT).show();
                    break;
                case DeviceScanCallBack.SCAN_FAILED_BLUETOOTH_DISABLE:
                    Toast.makeText(mContext,"请打开手机蓝牙",Toast.LENGTH_SHORT).show();
                    break;
                case DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE:
                    Toast.makeText(mContext,"请授予位置权限以扫描周围的蓝牙设备",Toast.LENGTH_SHORT).show();
                    break;
                case DeviceScanCallBack.SCAN_FAILED_LOCATION_PERMISSION_DISABLE_FOREVER:
                    Toast.makeText(mContext,"位置权限被您永久拒绝,请在设置里授予位置权限以扫描周围的蓝牙设备",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onStartSuccess() {
        }
    };
  ```
#### 连接外设
```
OKBLEDevice okbleDevice=new OKBLEDeviceImp(mContext,bleScanResult);
//okbleDevice=new OKBLEDeviceImp(mContext);
//okbleDevice.setBluetoothDevice(mBluetoothDevice);
okbleDevice.addDeviceListener(this);
okbleDevice.connect(true);//true表示连接断开后OKBLE的会自动重连
```
#### APP主动断开连接
```
okbleDevice.disConnect(false); //false表示断开后不需要OKBLE的自动重连; disConnect断开后,可以使用okbleDevice.connect()重新连接回来
```
#### APP清除连接
```
okbleDevice.remove(); //remove会清除连接的外设信息; 重新连接前需要重新调用setBleScanResult/setBluetoothDevice 来设置外设信息
```
#### 数据通讯
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
##### 发送大数据(应用场景1:OAD/空中升级)
OAD原理其实就是把固件(如.bin文件)加载成byte[]数组,然后把byte[]数据分段发送给设备,以下代码演示了主要分段部分,OAD细节不展示(如每个包的整合),根据需求会有变化
```
        final int sendInterval=50;//每个包之间的发送间隔,有些手机会因为发送太快而导致蓝牙奔溃,OAD失败率很高,可以适当增大,如80-100;
        okbleDevice.setOperationInterval(sendInterval);

        byte[] oadValues=loadBytesFromFile(filePath);

        final int blockSize=20;//表示一个包发送20个字节
        final int blockCount= (int) Math.ceil(oadValues.length*1.0f/blockSize);//发送的总包数
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
                    Log.e("TAG"," OAD 进度:"+progress+" 剩余时间:"+leftSeconds +"秒");
                    
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
#### APP模拟成外设(可被扫描并连接)
```
        OKBLEAdvertiseManager okbleAdvertiseManager;
        OKBLEServerDevice serverDevice;

        okbleAdvertiseManager=new OKBLEAdvertiseManager(this);
        serverDevice=new OKBLEServerDeviceImp(this);

        OKBLEAdvertiseSettings settings= new OKBLEAdvertiseSettings.Builder().setConnectable(true).build();
        OKBLEAdvertiseData  data=new OKBLEAdvertiseData.Builder().setIncludeDeviceName(true).build();
        //开启广播
        okbleAdvertiseManager.startAdvertising(settings, data, new OKBLEAdvertiseCallback() {
            @Override
            public void onStartSuccess() {
                LogUtils.e("---onStartSuccess ---");
                Toast.makeText(mContext,"Advertising Success",Toast.LENGTH_SHORT).show();
                configServer();//配置service 和characteristic
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
#### 扫描iBeacon设备
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
#### APP模拟成iBeacon
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
#### 检测进入、退出iBeacon区域
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

### QQ讨论群 -  554029215

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
