package com.zzc.lib.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created : zzc
 * Time : 2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class EventDispatcher implements IEventDispatcher {
    public static String TAG = "IEventDispatcher";

    private AbsBleManager mAbsBleManager;
    private EventCallback mEventCallback;

    public EventDispatcher(AbsBleManager AbsBleManager, EventCallback eventCallback) {
        this.mAbsBleManager = AbsBleManager;
        this.mEventCallback = eventCallback;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        String intentAction;
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            intentAction = AbsBleManager.ACTION_GATT_CONNECTED;
            mEventCallback.onBluetoothConnected();
            Log.d(TAG, "Connected to GATT server.");
            Log.d(TAG, "Attempting to start service discovery:" +
                    gatt.discoverServices());
            mAbsBleManager.notifyData(intentAction);
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            intentAction = AbsBleManager.ACTION_GATT_DISCONNECTED;
            mAbsBleManager.disconnect();
            mAbsBleManager.close();
            mEventCallback.onBluetoothDisconected();
            Log.d(TAG, "Disconnected from GATT server.");
            mAbsBleManager.notifyData(intentAction);
        } else {
            mAbsBleManager.close();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.d(TAG, "onServicesDiscovered status success:" + (status == BluetoothGatt.GATT_SUCCESS));
        if (status == BluetoothGatt.GATT_SUCCESS) {
            mAbsBleManager.servicesDiscovered(gatt, status);
        } else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d(TAG, "onCharacteristicRead status success:" + (status == BluetoothGatt.GATT_SUCCESS));
        if (status == BluetoothGatt.GATT_SUCCESS) {
            mAbsBleManager.notifyData(characteristic);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d(TAG, "onCharacteristicWrite status success:" + (status == BluetoothGatt.GATT_SUCCESS));
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "onCharacteristicChanged -- available data");
        mAbsBleManager.notifyData(characteristic);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {

    }
}
