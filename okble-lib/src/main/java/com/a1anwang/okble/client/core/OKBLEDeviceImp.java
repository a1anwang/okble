package com.a1anwang.okble.client.core;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.a1anwang.okble.client.scan.BLEScanResult;
import com.a1anwang.okble.common.BLEOperationQueue;
import com.a1anwang.okble.common.CommonUUIDUtils;
import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble.common.OKBLECharacteristicModel;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble.common.OKBLEException;
import com.a1anwang.okble.common.OKBLEServiceModel;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.a1anwang.okble.client.core.OKBLEOperation.BaseOperationListener.Operation_FAILED_BLE_Failed;
import static com.a1anwang.okble.client.core.OKBLEOperation.BaseOperationListener.Operation_FAILED_Overtime;
import static com.a1anwang.okble.client.core.OKBLEOperation.OperationType.OperationType_Read;


/**
 * Created by a1anwang.com on 2017/6/26.
 * contact：www.a1anwang.com
 */

public class OKBLEDeviceImp implements OKBLEDevice {
    private String TAG = "OKBLE";

    private String deviceTAG = "";


    /**
     * 操作超时时间
     */
    private  int OperationOverTime = 3*1000;

    /**
     * 操作间隔时间
     */
    private  int OperationInterval=30;

    /**
     * 是否需要在连接断开的时候自动重连
     */
    private  boolean autoReconnect;


    private DeviceStatus deviceStatus =DeviceStatus.DEVICE_STATUS_INITIAL;

    @Override
    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;

    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private BLEScanResult bleScanResult;

    private BLEOperationQueue<OKBLEOperation> bleOperationQueue;
    private List<BluetoothGattService> bluetoothGattServices;
    private HashMap<String, BluetoothGattCharacteristic> characteristicHashMap = new HashMap<>();



    private List<OKBLEDeviceListener> OKBLEDeviceListeners;


    private final  int Default_MTU=23;
    private int mtu=Default_MTU;


    @Override
    public void addDeviceListener( OKBLEDeviceListener OKBLEDeviceListener) {
        if(OKBLEDeviceListeners ==null){
            OKBLEDeviceListeners =new ArrayList<>();
        }
        OKBLEDeviceListeners.add(OKBLEDeviceListener);
    }

    @Override
    public void removeDeviceListener(OKBLEDeviceListener OKBLEDeviceListener) {
        if(OKBLEDeviceListeners !=null){
            OKBLEDeviceListeners.remove(OKBLEDeviceListener);
        }
    }

    @Override
    public void clearDeviceListener() {
        if(OKBLEDeviceListeners !=null){
            OKBLEDeviceListeners.clear();
        }
    }

    @Override
    public void clearOperations() {
        if(bleOperationQueue!=null){
            bleOperationQueue.clear();
        }
    }

    public String getMacAddress() {
        if (this.bluetoothDevice != null) {
            return this.bluetoothDevice.getAddress();
        }
        return null;
    }



    @Override
    public void setDeviceTAG(String deviceTAG) {
        this.deviceTAG = deviceTAG;
    }

    @Override
    public String getDeviceTAG() {
        return deviceTAG;
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return this.bluetoothDevice;
    }


    public OKBLEDeviceImp(Context context) {
        this.context = context;
        bleOperationQueue=new BLEOperationQueue<>();
    }

    public OKBLEDeviceImp(Context context, BLEScanResult bleScanResult) {
        this.context = context;
        this.bleScanResult = bleScanResult;
        this.bluetoothDevice = bleScanResult.getBluetoothDevice();
        if (deviceTAG==null||deviceTAG.equals("")) {
            this.deviceTAG = getMacAddress();
        }
        deviceStatus = DeviceStatus.DEVICE_STATUS_DISCONNECTED;

        bleOperationQueue=new BLEOperationQueue<>();
    }


    @Override
    public void setOperationOverTime(int overTime) {
        this.OperationOverTime=overTime;
    }

    @Override
    public void setOperationInterval(int interval) {
        this.OperationInterval=interval;
    }

    @Override
    public void setBleScanResult(BLEScanResult bleScanResult) {
        reSet();
        this.bleScanResult = bleScanResult;
        this.bluetoothDevice = bleScanResult.getBluetoothDevice();
        if (deviceTAG==null||deviceTAG.equals("")) {
            deviceTAG = getMacAddress();
        }
        deviceStatus = DeviceStatus.DEVICE_STATUS_DISCONNECTED;
    }

    @Override
    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        reSet();
        this.bluetoothDevice = bluetoothDevice;
        if (deviceTAG==null||deviceTAG.equals("")) {
            deviceTAG = getMacAddress();
        }
        deviceStatus = DeviceStatus.DEVICE_STATUS_DISCONNECTED;

    }

    @Override
    public boolean disConnect(boolean autoReconnect) {
        this.autoReconnect=autoReconnect;
        if(!autoReconnect){
            handler.removeCallbacks(connectGattRunnable);
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            return true;
        }
        return false;
    }

    public boolean isConnected() {
        if (deviceStatus==DeviceStatus.DEVICE_STATUS_CONNECTED) {
            return true;
        }
        return false;
    }

    @Override
    public boolean connect(boolean autoReconnect) {
        this.autoReconnect=autoReconnect;
        if(deviceStatus==DeviceStatus.DEVICE_STATUS_CONNECTED){
            return true;
        }
        if(deviceStatus==DeviceStatus.DEVICE_STATUS_CONNECTING){
            return true;
        }
        return doConnect();
    }

    private boolean doConnect(){

        if (bluetoothDevice != null) {
            deviceStatus =DeviceStatus. DEVICE_STATUS_CONNECTING;
            if (mBluetoothGatt != null) {

                refreshGatt(mBluetoothGatt);
            }
            handler.removeCallbacks(connectGattRunnable);
            handler.postDelayed(connectGattRunnable, 300);
            return true;
        } else {
            LogUtils.e("the bluetoothDevice is null, please reset the bluetoothDevice");
            return false;
        }
    }

    private void doReconnect() {
        handler.removeCallbacks(connectGattRunnable);
        handler.postDelayed(connectGattRunnable, 300);
    }

    public void connectComplete(){
        handler.removeCallbacks(connectGattRunnable);
    }

    Runnable connectGattRunnable = new Runnable() {

        @Override
        public void run() {
            if (bluetoothDevice != null && !isConnected()) {
                reSet();
                mBluetoothGatt = bluetoothDevice.connectGatt(context, false,
                        gattCallback);
                LogUtils.e("----connectGatt-----");
                handler.postDelayed(this,30*1000);
            } else {
                throw new OKBLEException("the bluetoothDevice is null, please reset the bluetoothDevice");
            }
        }
    };


    private boolean refreshGatt(BluetoothGatt gatt) {
        BluetoothGatt localGatt = gatt;
        try {
            Method localMethod = localGatt.getClass().getMethod("refresh",
                    new Class[0]);
            if (localMethod != null) {
                boolean result = ((Boolean) localMethod.invoke(localGatt,
                        new Object[0])).booleanValue();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<OKBLEServiceModel> getServiceModels() {
        if(!isConnected()) {
            return null;
        }
        if(bluetoothGattServices==null ||bluetoothGattServices.size()<=0){
            bluetoothGattServices=mBluetoothGatt.getServices();
        }
        if(bluetoothGattServices==null ||bluetoothGattServices.size()<=0){
            return null;
        }

        List<OKBLEServiceModel> okbleServiceModels=new ArrayList<>();

        for (BluetoothGattService service:bluetoothGattServices){
            OKBLEServiceModel serviceModel=new OKBLEServiceModel(service.getUuid().toString());
            serviceModel.setDesc(CommonUUIDUtils.getUUIDDesc(serviceModel.getUuid()));
            for (BluetoothGattCharacteristic characteristic: service.getCharacteristics()){
                OKBLECharacteristicModel characteristicModel=new OKBLECharacteristicModel(characteristic.getUuid().toString());
                characteristicModel.setDesc(CommonUUIDUtils.getUUIDDesc(characteristicModel.getUuid()));

                if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_WRITE)!=0){
                    characteristicModel.setCanWrite(true);
                }
                if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)!=0){
                    characteristicModel.setCanWriteNoResponse(true);
                }
                if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_READ)!=0){
                    characteristicModel.setCanRead(true);
                }
                if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_NOTIFY)!=0){
                    characteristicModel.setCanNotify(true);
                }
                if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_INDICATE)!=0){
                    characteristicModel.setCanIndicate(true);
                }
                serviceModel.addCharacteristicModel(characteristicModel);
            }
            okbleServiceModels.add(serviceModel);
        }
        return okbleServiceModels;
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            LogUtils.e(TAG, " onConnectionStateChange status:" + status + " newState:" + newState);
            bleOperationQueue.clear();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {// 连接成功
                    gatt.discoverServices();
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {// 断开连接
                    deviceStatus = DeviceStatus.DEVICE_STATUS_DISCONNECTED;
                    reSet();



                    if (OKBLEDeviceListeners != null) {
                        LogUtils.e(" OKBLEDeviceListeners size:"+ OKBLEDeviceListeners.size());
                        for (OKBLEDeviceListener listener: OKBLEDeviceListeners){
                            listener.onDisconnected(deviceTAG);
                        }
                    }
                    if(autoReconnect){
                        doReconnect();
                    }
                }
            } else {
                deviceStatus = DeviceStatus.DEVICE_STATUS_DISCONNECTED;
                reSet();
                if (OKBLEDeviceListeners != null) {
                    LogUtils.e(" OKBLEDeviceListeners size:"+ OKBLEDeviceListeners.size());

                    for (OKBLEDeviceListener listener: OKBLEDeviceListeners){
                        listener.onDisconnected(deviceTAG);
                    }
                }

                if(autoReconnect){
                    doReconnect();
                }

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            bleOperationQueue.clear();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                deviceStatus = DeviceStatus.DEVICE_STATUS_CONNECTED;
                connectComplete();
                bluetoothGattServices = mBluetoothGatt.getServices();
//                    for (BluetoothGattService server : bluetoothGattServices) {
//                        for (BluetoothGattCharacteristic characteristic : server.getCharacteristics()) {
//                            LogUtils.e(TAG,"characteristic "+characteristic.getUuid().toString());
//                        }
//                    }
                if (OKBLEDeviceListeners != null) {
                    for (OKBLEDeviceListener listener: OKBLEDeviceListeners){
                        listener.onConnected(deviceTAG);
                    }
                }

            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            LogUtils.e(TAG," onCharacteristicRead status:"+status +" characteristic:"+characteristic.getUuid().toString() +" value:"+OKBLEDataUtils.BytesToHexString(characteristic.getValue()));
            if(status==BluetoothGatt.GATT_SUCCESS){
                if(characteristic.getUuid().toString().equals(CommonUUIDUtils.Battery_Level)){
                    if (OKBLEDeviceListeners != null) {
                        for (OKBLEDeviceListener listener: OKBLEDeviceListeners){
                            listener.onReadBattery(deviceTAG,characteristic.getValue()[0]);
                        }
                    }
                }
            }
            if (OKBLEDeviceListeners != null) {
                for (OKBLEDeviceListener listener: OKBLEDeviceListeners){
                    listener.onReadValue(deviceTAG,characteristic.getUuid().toString(),characteristic.getValue(),status==BluetoothGatt.GATT_SUCCESS);
                }
            }

            if (bleOperationQueue.getOperationSize() > 0) {
                OKBLEOperation operation= bleOperationQueue.removeFirst();
                if(operation!=null&&operation.operationListener!=null){
                    if(status==BluetoothGatt.GATT_SUCCESS){
                        if(operation.operationListener instanceof OKBLEOperation.ReadOperationListener){
                            ((OKBLEOperation.ReadOperationListener)operation.operationListener).onReadValue(characteristic.getValue());
                        }
                    }else{
                        operation.operationListener.onFail(Operation_FAILED_BLE_Failed, OKBLEOperationFailedDescUtils.getDesc(status));
                    }
                }
                handler.removeCallbacks(nextRunnable);
                handler.postDelayed(nextRunnable,OperationInterval);
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            LogUtils.e(TAG," onCharacteristicWrite status:"+status +" characteristic:"+characteristic.getUuid().toString());
            if (OKBLEDeviceListeners != null) {
                for (OKBLEDeviceListener listener: OKBLEDeviceListeners){
                    listener.onWriteValue(deviceTAG,characteristic.getUuid().toString(),characteristic.getValue(),status==BluetoothGatt.GATT_SUCCESS);
                }
            }
            if (bleOperationQueue.getOperationSize() > 0) {
                OKBLEOperation operation= bleOperationQueue.removeFirst();
                if(operation!=null&&operation.operationListener!=null){
                    if(status==BluetoothGatt.GATT_SUCCESS){
                        if(operation.operationListener instanceof OKBLEOperation.WriteOperationListener){
                            ((OKBLEOperation.WriteOperationListener)operation.operationListener).onWriteValue(characteristic.getValue());
                        }
                    }else{
                        operation.operationListener.onFail(Operation_FAILED_BLE_Failed, OKBLEOperationFailedDescUtils.getDesc(status));
                    }
                }
                handler.removeCallbacks(nextRunnable);
                handler.postDelayed(nextRunnable,OperationInterval);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            LogUtils.e(TAG," onCharacteristicChanged characteristic:"+characteristic.getUuid().toString() +" value:"+ OKBLEDataUtils.BytesToHexString(characteristic.getValue()));
            if (OKBLEDeviceListeners != null) {
                for (OKBLEDeviceListener listener: OKBLEDeviceListeners){
                    listener.onReceivedValue(deviceTAG,characteristic.getUuid().toString(),characteristic.getValue());
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            LogUtils.e(TAG," onDescriptorWrite:"+status+" descriptor:"+descriptor.getUuid().toString()+" char:"+descriptor.getCharacteristic().getUuid().toString());
            if (bleOperationQueue.getOperationSize() > 0) {
                OKBLEOperation operation= bleOperationQueue.removeFirst();
                if(operation!=null&&operation.operationListener!=null){
                    if(status==BluetoothGatt.GATT_SUCCESS){
                        if(operation.operationListener instanceof OKBLEOperation.NotifyOrIndicateOperationListener){
                            ((OKBLEOperation.NotifyOrIndicateOperationListener)operation.operationListener).onNotifyOrIndicateComplete();
                        }
                    }else{
                        operation.operationListener.onFail(Operation_FAILED_BLE_Failed, OKBLEOperationFailedDescUtils.getDesc(status));
                    }
                }

                handler.removeCallbacks(nextRunnable);
                handler.postDelayed(nextRunnable,OperationInterval);
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            LogUtils.e("onMtuChanged mtu:"+mtu);
            if (bleOperationQueue.getOperationSize() > 0) {
                OKBLEOperation operation= bleOperationQueue.removeFirst();
                if(operation!=null&&operation.operationListener!=null){
                    if(status==BluetoothGatt.GATT_SUCCESS){
                        if(operation.operationListener instanceof OKBLEOperation.ChangeMTUListener){
                            ((OKBLEOperation.ChangeMTUListener)operation.operationListener).onMtuChange(mtu);
                        }
                    }else{
                        operation.operationListener.onFail(Operation_FAILED_BLE_Failed, OKBLEOperationFailedDescUtils.getDesc(status));
                    }
                }

                handler.removeCallbacks(nextRunnable);
                handler.postDelayed(nextRunnable,OperationInterval);
            }
        }
    };


    Runnable nextRunnable=new Runnable() {
        @Override
        public void run() {
            if (bleOperationQueue.getOperationSize() > 0) {
                doNextBleOperation();
            }
        }
    };
    private void reSet() {
        if(deviceTAG!=null && bluetoothDevice!=null&& deviceTAG.equals(getMacAddress())){
            //deviceTAG是默认的mac地址的话,reset时候,重置deviceTAG
            deviceTAG="";
        }
        bleOperationQueue.clear();
        handler.removeCallbacks(nextRunnable);
        handler.removeCallbacks(connectGattRunnable);
        if(bluetoothGattServices!=null)
        bluetoothGattServices.clear();
        characteristicHashMap.clear();
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            refreshGatt(mBluetoothGatt);
            mBluetoothGatt = null;
        }
    }
    public void remove() {
        handler.removeCallbacks(connectGattRunnable);
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt.disconnect();
            refreshGatt(mBluetoothGatt);
            mBluetoothGatt = null;
        }
        reSet();
        deviceStatus =DeviceStatus.DEVICE_STATUS_INITIAL;
        bleScanResult =null;
        bluetoothDevice = null;

    }



    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void addChangeMTUOperation(int mtu,OKBLEOperation.ChangeMTUListener changeMTUListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if (!isConnected()) {
                String errMsg="change mtu failed, device not connected";
                LogUtils.e(TAG,errMsg);
                if(changeMTUListener!=null){
                    changeMTUListener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Device_Not_Connected,errMsg);
                }
                return;
            }
            if(mtu<Default_MTU){
                String errMsg="change mtu failed, mtu invalid";
                LogUtils.e(TAG,errMsg);
                if(changeMTUListener!=null){
                    changeMTUListener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Device_Not_Connected,errMsg);
                }
                return;
            }

            OKBLEOperation okbleOperation = new OKBLEOperation();
            okbleOperation.mtu=mtu;
            okbleOperation.operationType =OKBLEOperation. OperationType.OperationType_Change_MTU;
            okbleOperation.operationListener=changeMTUListener;
            bleOperationQueue.add(okbleOperation);
            checkNextBleOperation();

        }else{
            String errMsg="change mtu failed, need android api 21";
            LogUtils.e(TAG,errMsg);
            if(changeMTUListener!=null){
                changeMTUListener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Other,errMsg);
            }
        }
    }

    public void addWriteOperation(String characteristicUUID, String value, OKBLEOperation.WriteOperationListener listener) {
       addWriteOperation(characteristicUUID,OKBLEDataUtils.hexStringToBytes(value),listener);
    }
    public void addWriteOperation(String characteristicUUID, byte[] value,OKBLEOperation.WriteOperationListener listener){
        if (!isConnected()) {
            String errMsg="addWriteOperation failed, device not connected";
            LogUtils.e(TAG,errMsg);
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Device_Not_Connected,errMsg);
            }
            return;
        }
        if(value==null){

            String errMsg="addWriteOperation failed, value is null";
            LogUtils.e(TAG,errMsg);
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Null_Value,errMsg);
            }
            return;
        }

        characteristicUUID = characteristicUUID.trim().toLowerCase();
        if (!OKBLEDataUtils.isValidUUID(characteristicUUID) && characteristicUUID.length() != 4) {

            String errMsg="characteristicUUID not valid";
            LogUtils.e(TAG,errMsg);
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Invalid_UUID,errMsg);
            }
            return;
        }
        BluetoothGattCharacteristic characteristic = characteristicHashMap.get(characteristicUUID);
        if (characteristic == null) {
            characteristic= findCharacteristic(characteristicUUID);
            if(characteristic!=null){
                characteristicHashMap.put(characteristicUUID,characteristic);
            }
        }
        if (characteristic != null) {
            if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_WRITE)!=0 ){
                OKBLEOperation okbleOperation = new OKBLEOperation();
                okbleOperation.operationType = OKBLEOperation.OperationType.OperationType_Write;
                okbleOperation.bleChar = characteristic;
                okbleOperation.value = value;
                okbleOperation.operationListener=listener;
                bleOperationQueue.add(okbleOperation);
                checkNextBleOperation();
            }else if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)!=0){
                OKBLEOperation okbleOperation = new OKBLEOperation();
                okbleOperation.operationType = OKBLEOperation.OperationType.OperationType_Write_No_Response;
                okbleOperation.bleChar = characteristic;
                okbleOperation.value = value;
                okbleOperation.operationListener=listener;
                bleOperationQueue.add(okbleOperation);
                checkNextBleOperation();
            } else{
                String errMsg="addWriteOperation failed, write property not found";
                LogUtils.e(TAG,errMsg);
                if(listener!=null){
                    listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Characteristic_Property_Not_Found,errMsg);
                }
                return;
            }

        }else{
            String errMsg="addWriteOperation failed, characteristic not found";
            LogUtils.e(TAG,errMsg );
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Characteristic_Not_Found,errMsg);
            }
            return;
        }
    }



    public void addReadOperation(String characteristicUUID,OKBLEOperation.ReadOperationListener listener){
        if (!isConnected()) {
            String errMsg="addReadOperation failed, device not connected";
            LogUtils.e(TAG, errMsg);
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Device_Not_Connected,errMsg);
            }
            return;
        }
        characteristicUUID = characteristicUUID.trim().toLowerCase();
        if (!OKBLEDataUtils.isValidUUID(characteristicUUID) && characteristicUUID.length() != 4) {
            String errMsg="characteristicUUID not valid ";
            LogUtils.e(TAG,errMsg);
            if(listener!=null){

                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Invalid_UUID,errMsg);
            }
            return;
        }
        BluetoothGattCharacteristic characteristic = characteristicHashMap.get(characteristicUUID);
        if (characteristic == null) {
            characteristic= findCharacteristic(characteristicUUID);
            if(characteristic!=null){
                characteristicHashMap.put(characteristicUUID,characteristic);
            }
        }
        if (characteristic != null) {

            if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_READ)!=0 ){
                OKBLEOperation okbleOperation = new OKBLEOperation();
                okbleOperation.operationType = OperationType_Read;
                okbleOperation.bleChar = characteristic;
                okbleOperation.operationListener=listener;
                bleOperationQueue.add(okbleOperation);
                checkNextBleOperation();
            }else{

                String errMsg="addReadOperation failed, read property not found";
                LogUtils.e(TAG, errMsg);
                if(listener!=null){
                    listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Characteristic_Property_Not_Found,errMsg);
                }
                return;
            }
        }else{
            String errMsg="addReadOperation failed, characteristic not found";
            LogUtils.e(TAG,errMsg );
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Characteristic_Not_Found,errMsg);
            }
            return;
        }
    }
    public void addNotifyOrIndicateOperation(String characteristicUUID,boolean enable,OKBLEOperation.NotifyOrIndicateOperationListener listener){
        if (!isConnected()) {
            String errMsg="addNotifyOrIndicateOperation failed, device not connected";
            LogUtils.e(TAG, errMsg);
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Device_Not_Connected,errMsg);
            }
            return;
        }
        characteristicUUID = characteristicUUID.trim().toLowerCase();
        if (!OKBLEDataUtils.isValidUUID(characteristicUUID) && characteristicUUID.length() != 4) {

            String errMsg="characteristicUUID not valid ";
            LogUtils.e(TAG, errMsg);
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Invalid_UUID,errMsg);
            }
            return;
        }
        BluetoothGattCharacteristic characteristic = characteristicHashMap.get(characteristicUUID);
        if (characteristic == null) {
            characteristic= findCharacteristic(characteristicUUID);
            if(characteristic!=null){
                characteristicHashMap.put(characteristicUUID,characteristic);
            }
        }
        if (characteristic != null) {

            if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_NOTIFY)!=0 ){
                OKBLEOperation okbleOperation = new OKBLEOperation();
                if(enable){
                    okbleOperation.operationType =enable? OKBLEOperation.OperationType.OperationType_Enable_Notify: OKBLEOperation.OperationType.OperationType_Disable_Notify;
                }
                okbleOperation.bleChar = characteristic;
                okbleOperation.operationListener=listener;
                bleOperationQueue.add(okbleOperation);
                checkNextBleOperation();
            }else if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_INDICATE)!=0){
                OKBLEOperation okbleOperation = new OKBLEOperation();
                if(enable){
                    okbleOperation.operationType =enable? OKBLEOperation.OperationType.OperationType_Enable_Indicate: OKBLEOperation.OperationType.OperationType_Disable_Indicate;
                }
                okbleOperation.bleChar = characteristic;
                okbleOperation.operationListener=listener;
                bleOperationQueue.add(okbleOperation);
                checkNextBleOperation();
            }else{
                String errMsg="addNotifyOrIndicateOperation failed, Notify or Indicate property not found";
                LogUtils.e(TAG, errMsg);
                if(listener!=null){
                    listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Characteristic_Property_Not_Found,errMsg);
                }
                return ;
            }

        }else{
            String errMsg="addNotifyOrIndicateOperation failed, characteristic not found";
            LogUtils.e(TAG, errMsg);
            if(listener!=null){
                listener.onFail(OKBLEOperation.BaseOperationListener.Operation_FAILED_Characteristic_Not_Found,errMsg);
            }
        }
    }




    private BluetoothGattCharacteristic findCharacteristic(String uuid) {
        if (bluetoothGattServices == null || bluetoothGattServices.size() == 0) {
            return null;
        }
        uuid =uuid.toLowerCase();
        String entireUUID=uuid;
        if(entireUUID.length()==4){
            entireUUID= CommonUUIDUtils.CommonUUIDStr_x.replace("xxxx", uuid);
        }
        LogUtils.e(TAG," entireUUID:"+entireUUID);
        for (BluetoothGattService server : bluetoothGattServices) {
            for (BluetoothGattCharacteristic characteristic : server.getCharacteristics()) {
                if (characteristic.getUuid().toString().equals(entireUUID)) {
                    return characteristic;
                }
            }
        }
        return null;
    }


    synchronized private void checkNextBleOperation() {
        if (bleOperationQueue.getOperationSize() == 1) {
            OKBLEOperation okbleOperation = bleOperationQueue.get(0);
            doBleOperation(okbleOperation);
        }
    }

    synchronized private void  doBleOperation(OKBLEOperation okbleOperation) {

        LogUtils.e(TAG, " BleOperation size:" + bleOperationQueue.getOperationSize());
        if (!isConnected()) {
            LogUtils.e(TAG, "doBleOperation failed, device not connected");
            return;
        }
        switch (okbleOperation.operationType) {
            case OperationType_Read:{
                LogUtils.e(TAG, " read:" + okbleOperation.bleChar.getUuid().toString());
                boolean success= readCharacteristic(okbleOperation.bleChar);
                if(success){
                    if(okbleOperation.operationListener!=null){
                        okbleOperation.operationListener.onExecuteSuccess(okbleOperation.operationType);
                    }
                }else{
                    if(okbleOperation.operationListener!=null){
                        okbleOperation.operationListener.onFail(Operation_FAILED_BLE_Failed,"BLE_Failed");
                    }
                    bleOperationQueue.removeFirst();
                    doNextBleOperation();
                }

                break;}
            case OperationType_Enable_Notify:
            case OperationType_Enable_Indicate: {
                LogUtils.e(TAG, " enableNotification/Indication:"
                        + okbleOperation.bleChar.getUuid().toString());

                boolean success = setNotificationOrIndication(true, okbleOperation.bleChar);
                if (success) {
                    if (okbleOperation.operationListener != null) {
                        okbleOperation.operationListener.onExecuteSuccess(okbleOperation.operationType);
                    }
                } else {
                    if (okbleOperation.operationListener != null) {
                        okbleOperation.operationListener.onFail(Operation_FAILED_BLE_Failed, "BLE_Failed");
                    }
                    bleOperationQueue.removeFirst();
                    doNextBleOperation();
                }

                break;}
            case OperationType_Disable_Notify:
            case OperationType_Disable_Indicate:{
                LogUtils.e(TAG, " disableNotification/Indication:"
                        + okbleOperation.bleChar.getUuid().toString());
               boolean success= setNotificationOrIndication(false, okbleOperation.bleChar);

                if (success) {
                    if (okbleOperation.operationListener != null) {
                        okbleOperation.operationListener.onExecuteSuccess(okbleOperation.operationType);
                    }
                } else {
                    if (okbleOperation.operationListener != null) {
                        okbleOperation.operationListener.onFail(Operation_FAILED_BLE_Failed, "BLE_Failed");
                    }
                    bleOperationQueue.removeFirst();
                    doNextBleOperation();
                }
                break;}
            case OperationType_Write_No_Response:
            case OperationType_Write:{
                byte[] values = okbleOperation.value;
                boolean success= writeCharacteristic(okbleOperation.bleChar, values);
                if (success) {
                    if (okbleOperation.operationListener != null) {
                        okbleOperation.operationListener.onExecuteSuccess(okbleOperation.operationType);
                    }
                } else {
                    if (okbleOperation.operationListener != null) {
                        okbleOperation.operationListener.onFail(Operation_FAILED_BLE_Failed, "BLE_Failed");
                    }
                    bleOperationQueue.removeFirst();
                    doNextBleOperation();
                }
                break;}
            case OperationType_Change_MTU:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    LogUtils.e(TAG, " requestMtu :"+ okbleOperation.mtu);
                    boolean success=  mBluetoothGatt.requestMtu(okbleOperation.mtu);
                    if (success) {
                        if (okbleOperation.operationListener != null) {
                            okbleOperation.operationListener.onExecuteSuccess(okbleOperation.operationType);
                        }
                    } else {
                        if (okbleOperation.operationListener != null) {
                            okbleOperation.operationListener.onFail(Operation_FAILED_BLE_Failed, "BLE_Failed");
                        }
                        bleOperationQueue.removeFirst();
                        doNextBleOperation();
                    }
                }
                break;
            default:
                break;
        }

        handler.removeCallbacks(operationOverTimeRunnable);
        handler.postDelayed(operationOverTimeRunnable, OperationOverTime);
    }


    private Runnable operationOverTimeRunnable = new Runnable() {

        @Override
        public void run() {
            if (bleOperationQueue.getOperationSize() > 0) {
                OKBLEOperation operation= bleOperationQueue.removeFirst();
                if(operation!=null&&operation.operationListener!=null){
                    operation.operationListener.onFail(Operation_FAILED_Overtime,"failed,Overtime");
                }

                doNextBleOperation();
            }
        }
    };

    private void doNextBleOperation() {
        if (bleOperationQueue.getOperationSize() > 0) {
            OKBLEOperation okbleOperation = bleOperationQueue.get(0);

            doBleOperation(okbleOperation);
        }
    }

    private boolean readCharacteristic(
            BluetoothGattCharacteristic characteristic) {

        if (!isConnected()) {
            LogUtils.e(TAG, "readCharacteristic failed, device not connected");
            return false;
        }

        if (mBluetoothGatt == null) {
            LogUtils.e(TAG, "readCharacteristic failed, mBluetoothGatt is null");
            return false;
        }
        if (characteristic == null) {
            LogUtils.e(TAG, "readCharacteristic failed, characteristic is null");
            return false;
        }
        boolean b = mBluetoothGatt.readCharacteristic(characteristic);
        LogUtils.e(TAG, " readCharacteristic " + b);
        return b;
    }

    private boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] values) {
        if (!isConnected()) {
            LogUtils.e(TAG, "writeCharacteristic failed, device not connected");
            return false;
        }
        if (mBluetoothGatt == null) {
            LogUtils.e(TAG, "writeCharacteristic failed, mBluetoothGatt is null");
            return false;
        }
        if (characteristic == null) {
            LogUtils.e(TAG, "writeCharacteristic failed, characteristic is null");
            return false;
        }
        characteristic.setValue(values);
        if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)!=0){
            characteristic.setWriteType( BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        }else{
            characteristic.setWriteType( BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }
        boolean b = mBluetoothGatt.writeCharacteristic(characteristic);
        LogUtils.e(TAG, " writeCharacteristic " + b + ", value:" + OKBLEDataUtils.BytesToHexString(values));
        return b;
    }

    private boolean setNotificationOrIndication(boolean enable,
                                                BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            LogUtils.e(TAG, "setNotificationOrIndication failed, characteristic is null");
            return false;
        }
        if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_NOTIFY)==0
                &&(characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_INDICATE)==0){
            LogUtils.e(TAG, "setNotificationOrIndication failed, characteristic has no notification or indication function");
            return false;
        }
        if (!isConnected()) {
            LogUtils.e(TAG, "setNotificationOrIndication failed, device not connected");
            return false;
        }
        if (mBluetoothGatt == null) {
            LogUtils.e(TAG, "setNotificationOrIndication failed, mBluetoothGatt is null");
            return false;
        }

        if (!mBluetoothGatt.setCharacteristicNotification(characteristic,
                enable)) {
            LogUtils.e(TAG, "setNotificationOrIndication failed");
            return false;
        }

        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString(CommonUUIDUtils.Client_Characteristic_Configuration));

        if (clientConfig == null) {
            LogUtils.e(TAG, "setNotificationOrIndication failed,clientConfig is null");
            return false;
        }
        byte[] configValue=null;
        if(enable){
            if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_NOTIFY)!=0){
                configValue=BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
            }else if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_INDICATE)!=0){
                configValue=BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
            }
        }else{
            configValue=BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        }
        boolean b = clientConfig.setValue(configValue);
        if (!b) {
            LogUtils.e(TAG, "setNotificationOrIndication failed,clientConfig setValue failed");
            return false;
        }
        b = mBluetoothGatt.writeDescriptor(clientConfig);
        LogUtils.e(TAG, "setNotificationOrIndication:" + b);
        return b;
    }

    public boolean isNotifyEnabled(String uuid){
        BluetoothGattCharacteristic characteristic=findCharacteristic(uuid);
        if(characteristic==null){
            LogUtils.e(TAG, "characteristic not found");

            return false;
        }
        if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_NOTIFY)!=0){
           BluetoothGattDescriptor descriptor= characteristic.getDescriptor(UUID.fromString(CommonUUIDUtils.Client_Characteristic_Configuration));
           if(descriptor!=null){
               byte[] value= descriptor.getValue();
               if(value!=null){
                   if(Arrays.equals(value,BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)){
                       return true;
                   }else{
                       return false;
                   }
               }
           }
        }
        return false;
    }
    public boolean isIndicateEnabled(String uuid){
        BluetoothGattCharacteristic characteristic=findCharacteristic(uuid);
        if(characteristic==null){
            LogUtils.e(TAG, "characteristic not found");

            return false;
        }
        if((characteristic.getProperties()&BluetoothGattCharacteristic.PROPERTY_INDICATE)!=0){
            BluetoothGattDescriptor descriptor= characteristic.getDescriptor(UUID.fromString(CommonUUIDUtils.Client_Characteristic_Configuration));
            if(descriptor!=null){
                byte[] value= descriptor.getValue();
                if(value!=null){
                    if(Arrays.equals(value,BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        }
        return false;
    }


    private  String formatTimeYMDHMSF(long time) {

        Timestamp ts = new Timestamp(time);

        return ts.toString();
    }
}
