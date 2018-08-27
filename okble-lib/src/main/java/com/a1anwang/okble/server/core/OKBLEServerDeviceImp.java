package com.a1anwang.okble.server.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.a1anwang.okble.common.BLEOperationQueue;
import com.a1anwang.okble.common.CommonUUIDUtils;
import com.a1anwang.okble.common.LogUtils;
import com.a1anwang.okble.common.OKBLECharacteristicModel;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble.common.OKBLEServiceModel;

import java.util.List;
import java.util.UUID;

import static com.a1anwang.okble.server.core.OKBLEServerOperation.BLEServerOperationListener.Operation_FAILED_BLE_Failed;
import static com.a1anwang.okble.server.core.OKBLEServerOperation.BLEServerOperationListener.Operation_FAILED_Invalid_Characteristic_UUID;
import static com.a1anwang.okble.server.core.OKBLEServerOperation.BLEServerOperationListener.Operation_FAILED_Invalid_Service_UUID;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class OKBLEServerDeviceImp implements  OKBLEServerDevice{
    private Handler handler = new Handler(Looper.getMainLooper());

    private final  int OperationInterval=50;
    private BLEOperationQueue<OKBLEServerOperation> bleOperationQueue;
    private Context context;
    private BluetoothGattServer gattServer;

    BluetoothManager bluetoothManager;

    public OKBLEServerDeviceImp(Context context){
        this.context=context;
        bleOperationQueue=new BLEOperationQueue<>();
        bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);

    }

    private void openGattServer(){
        gattServer = bluetoothManager.openGattServer(context, gattServerCallback);
    }

    @Override
    public void reSet() {
        if(gattServer!=null){
            gattServer.clearServices();
            gattServer.close();
        }
        gattServer=null;
    }

    @Override
    public void addCharacteristic(List<OKBLECharacteristicModel> okbleCharacteristicModels, OKBLEServiceModel okbleServiceModel, OKBLEServerOperation.BLEServerOperationListener operationListener) {
        if(gattServer==null){
            openGattServer();
        }
        if(!OKBLEDataUtils.isValidUUID(okbleServiceModel.getUuid())){
            if(operationListener!=null){
                operationListener.onAddCharacteristicFailed(Operation_FAILED_Invalid_Service_UUID,OKBLEServerOperationFailedDescUtils.getDesc(Operation_FAILED_Invalid_Service_UUID));
            }
            return;
        }
        for (OKBLECharacteristicModel okbleCharacteristicModel:okbleCharacteristicModels){
            if(!OKBLEDataUtils.isValidUUID(okbleCharacteristicModel.getUuid())){
                if(operationListener!=null){
                    operationListener.onAddCharacteristicFailed(Operation_FAILED_Invalid_Characteristic_UUID,OKBLEServerOperationFailedDescUtils.getDesc(Operation_FAILED_Invalid_Characteristic_UUID));
                }
                return;
            }
        }


        OKBLEServerOperation operation=new OKBLEServerOperation();
        operation.characteristicModels=okbleCharacteristicModels;
        operation.serviceModel=okbleServiceModel;
        operation.operationListener=operationListener;

        bleOperationQueue.add(operation);
        checkNextBleOperation();
    }
    synchronized private void checkNextBleOperation() {
        if (bleOperationQueue.getOperationSize() == 1) {
            OKBLEServerOperation bleOperation = bleOperationQueue.getFirst();
            doBleOperation(bleOperation);
        }
    }
    synchronized private void  doBleOperation(OKBLEServerOperation bleOperation) {

        BluetoothGattService bluetoothGattService;

        bluetoothGattService=new BluetoothGattService(UUID.fromString(bleOperation.serviceModel.getUuid()),
                    BluetoothGattService.SERVICE_TYPE_PRIMARY);

        for (OKBLECharacteristicModel characteristicModel:bleOperation.characteristicModels){
            BluetoothGattCharacteristic characteristic=createCharacteristic(characteristicModel);
            bluetoothGattService.addCharacteristic(characteristic);
        }


        gattServer.addService(bluetoothGattService);


    }

    private BluetoothGattCharacteristic createCharacteristic(OKBLECharacteristicModel characteristicModel){
        int properties=0;
        int permissions=0;
        if(characteristicModel.isCanIndicate()){
            properties|=BluetoothGattCharacteristic.PROPERTY_INDICATE;
        }
        if(characteristicModel.isCanNotify()){
            properties|=BluetoothGattCharacteristic.PROPERTY_NOTIFY;
        }
        if(characteristicModel.isCanRead()){
            properties|=BluetoothGattCharacteristic.PROPERTY_READ;
            permissions|=BluetoothGattCharacteristic.PERMISSION_READ;
        }
        if(characteristicModel.isCanWrite()){
            properties|=BluetoothGattCharacteristic.PROPERTY_WRITE;
        }
        if(characteristicModel.isCanWriteNoResponse()){
            properties|=BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
        }

        if(characteristicModel.isCanWrite()||characteristicModel.isCanWriteNoResponse()){
            permissions|=BluetoothGattCharacteristic.PERMISSION_WRITE;
        }

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(characteristicModel.getUuid()),properties,permissions );
        if(characteristicModel.isCanIndicate()||characteristicModel.isCanNotify()){
            characteristic.addDescriptor(new BluetoothGattDescriptor(UUID.fromString(CommonUUIDUtils.Client_Characteristic_Configuration), BluetoothGattDescriptor.PERMISSION_WRITE));
        }
        return characteristic;
    }

    private BluetoothGattServerCallback gattServerCallback=new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            LogUtils.e("---- server onConnectionStateChange status:"+status+" newState:"+newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);

            LogUtils.e("---- onServiceAdded---- ");
            for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                LogUtils.e("---- characteristic:"+characteristic.getUuid().toString());
            }
            if (bleOperationQueue.getOperationSize() > 0) {
                OKBLEServerOperation operation= bleOperationQueue.removeFirst();
                if(operation!=null&&operation.operationListener!=null){
                    if(status== BluetoothGatt.GATT_SUCCESS){
                        operation.operationListener.onAddCharacteristicSuccess();

                    }else{
                        operation.operationListener.onAddCharacteristicFailed(Operation_FAILED_BLE_Failed, OKBLEServerOperationFailedDescUtils.getDesc(status));
                    }
                }
                handler.removeCallbacks(nextRunnable);
                handler.postDelayed(nextRunnable,OperationInterval);
            }


        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }

        @Override
        public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(device, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyRead(device, txPhy, rxPhy, status);
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
    private void doNextBleOperation() {
        if (bleOperationQueue.getOperationSize() > 0) {
            OKBLEServerOperation okbleServerOperation = bleOperationQueue.getFirst();

            doBleOperation(okbleServerOperation);
        }
    }

}
