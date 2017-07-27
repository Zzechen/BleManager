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

public interface EventCallback {
    boolean isBluetoothConnected();

    void onBluetoothConnected();

    void onServiceConnected(ComponentName componentName,
                            IBinder service);

    void onServiceDisconnected();

    void onBluetoothDisconected();

    void onGATTServiceDiscovered(BluetoothGattService bluetoothGattService);

    void onDataAvailable(Intent intent);
}
