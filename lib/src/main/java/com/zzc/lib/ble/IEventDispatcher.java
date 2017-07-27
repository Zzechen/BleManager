package com.zzc.lib.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created : zzc
 * Time : 2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public interface IEventDispatcher {
    void onConnectionStateChange(BluetoothGatt gatt, int status,
                                 int newState);

    void onServicesDiscovered(BluetoothGatt gatt, int status);

    void onCharacteristicRead(BluetoothGatt gatt,
                              BluetoothGattCharacteristic characteristic, int status);

    void onCharacteristicWrite(BluetoothGatt gatt,
                               BluetoothGattCharacteristic characteristic, int status);

    void onCharacteristicChanged(BluetoothGatt gatt,
                                 BluetoothGattCharacteristic characteristic);

    void onDescriptorWrite(BluetoothGatt gatt,
                           BluetoothGattDescriptor descriptor, int status);

    void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
}
