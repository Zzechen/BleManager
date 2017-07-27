package com.zzc.lib.ble;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created : zzc
 * Time : 2017/4/25
 * Email : zzc1259@163.com
 * Description : ${蓝牙事件处理者}
 */

public interface BluetoothObserver {
    void update(String action);

    void update(String src, BluetoothGattCharacteristic characteristic);
}
