package com.zzc.lib.ble;

import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created : zzc
 * Time : 2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class DefaultEventCallback implements EventCallback {
    @Override
    public boolean isBluetoothConnected() {
        return false;
    }

    @Override
    public void onBluetoothConnected() {

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onBluetoothDisconected() {

    }

    @Override
    public void onGATTServiceDiscovered(BluetoothGattService bluetoothGattService) {

    }

    @Override
    public void onDataAvailable(Intent intent) {

    }
}
