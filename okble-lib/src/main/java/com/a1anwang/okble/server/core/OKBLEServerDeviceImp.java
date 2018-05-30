package com.a1anwang.okble.server.core;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import com.a1anwang.okble.common.BLEOperationQueue;
import com.a1anwang.okble.common.CommonUUIDUtils;
import com.a1anwang.okble.common.OKBLECharacteristicModel;
import com.a1anwang.okble.common.OKBLEDataUtils;
import com.a1anwang.okble.common.OKBLEServiceModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by a1anwang.com on 2018/5/30.
 */

public class OKBLEServerDeviceImp implements  OKBLEServerDevice{

    private BLEOperationQueue<OKBLEServerOperation> bleOperationQueue;
    private Context context;
    private BluetoothGattServer gattServer;

    private Map<String,BluetoothGattService> bluetoothGattServiceHashMap;


    public OKBLEServerDeviceImp(Context context){
        this.context=context;
        bleOperationQueue=new BLEOperationQueue<>();
        BluetoothManager bluetoothManager = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        gattServer = bluetoothManager.openGattServer(context, gattServerCallback);

    }

    @Override
    public void addCharacteristic(OKBLECharacteristicModel okbleCharacteristicModel, OKBLEServiceModel okbleServiceModel) {
        if(OKBLEDataUtils.isValidUUID(okbleCharacteristicModel.getUuid())){

        }
        OKBLEServerOperation operation=new OKBLEServerOperation();
        bleOperationQueue.add(operation);
        checkNextBleOperation();
    }
    synchronized private void checkNextBleOperation() {
        if (bleOperationQueue.getOperationSize() == 1) {
            OKBLEServerOperation bleOperation = bleOperationQueue.get(0);
            doBleOperation(bleOperation);
        }
    }
    synchronized private void  doBleOperation(OKBLEServerOperation bleOperation) {
        if(bluetoothGattServiceHashMap==null){
            bluetoothGattServiceHashMap=new HashMap<>();
        }
        BluetoothGattService bluetoothGattService;
        if(bluetoothGattServiceHashMap.containsKey(bleOperation.serviceModel.getUuid())){
            bluetoothGattService=bluetoothGattServiceHashMap.get(bleOperation.serviceModel.getUuid());
        }else{
            bluetoothGattService=new BluetoothGattService(UUID.fromString(bleOperation.serviceModel.getUuid()),
                    BluetoothGattService.SERVICE_TYPE_PRIMARY);

        }
        bluetoothGattServiceHashMap.put(bleOperation.serviceModel.getUuid(),bluetoothGattService);

        int properties

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(bleOperation.characteristicModel.getUuid()),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY +BluetoothGattCharacteristic.PROPERTY_WRITE +BluetoothGattCharacteristic.PROPERTY_READ ,
                BluetoothGattCharacteristic.PERMISSION_WRITE+BluetoothGattCharacteristic.PERMISSION_READ );
        characteristic3.addDescriptor(new BluetoothGattDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_WRITE));




    }
        private BluetoothGattServerCallback gattServerCallback=new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
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

}
