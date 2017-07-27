package com.zzc.lib.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created : zzc
 * Time : 2017/4/25, update:2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public abstract class AbsBleManager {
    public static String ACTION_GATT_CONNECTED = "com.zzc.ble.ACTION_GATT_CONNECTED";
    public static String ACTION_GATT_DISCONNECTED = "com.zzc.ble.ACTION_GATT_DISCONNECTED";
    public static String ACTION_GATT_SERVICES_DISCOVERED = "com.zzc.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public static String ACTION_DATA_AVAILABLE = "com.zzc.ble.ACTION_DATA_AVAILABLE";

    protected static String CODE_UUID_CLIENT = "00002902-0000-1000-8000-00805f9b34fb";

    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCOVERING = 2;
    public static final int STATE_CONNECTED = 3;

    protected String mUUIDHead;


    @IntDef({STATE_NONE, STATE_CONNECTING, STATE_DISCOVERING, STATE_CONNECTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {

    }

    private List<BluetoothObserver> mEventObservers = new ArrayList<>();

    public abstract boolean initialize();

    public abstract boolean isAvailable();

    public abstract boolean isConnected();

    @State
    public abstract int bleState();

    public abstract void open(Activity activity, BleOpenListener listener);

    /**
     * start scan
     *
     * @param leScanCallback scan result callback
     * @param stopScan       stop scan callback
     * @param duration       duration for scan
     */
    public abstract void scan(BluetoothAdapter.LeScanCallback leScanCallback, @Nullable final Runnable stopScan, long duration);

    /**
     * stop scan
     */
    public abstract void stopScan();

    /**
     * connect
     *
     * @param address
     */
    public abstract boolean connect(final String address);

    /**
     * disconnect
     */
    public abstract void disconnect();

    abstract void release();

    public void setServiceUUIDHead(String uuidHead) {
        mUUIDHead = uuidHead;
    }

    /**
     * 发送数据
     *
     * @param bytes       所要发送的数据
     * @param gattUUID    特征uuid
     * @param serviceUUID 服务uuid
     */
    public abstract void sendData(byte[] bytes, UUID gattUUID, UUID serviceUUID);

    /**
     * 读取数据
     *
     * @param characteristic
     */
    abstract void readCharacteristic(BluetoothGattCharacteristic characteristic);

    /**
     * 设置特征通知监听
     *
     * @param characteristic 所要监听的特征
     * @param enabled        true --> 接收通知
     */
    abstract void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled);

    /**
     * 扫描蓝牙设备中的服务与特征
     *
     * @param gatt   获取服务和特征
     * @param status
     */
    abstract void servicesDiscovered(BluetoothGatt gatt, int status);

    /**
     * 关闭连接 释放资源
     * note：防止status 133
     */
    abstract void close();

    /**
     * @param observer
     */
    public void registerBluetooth(BluetoothObserver observer) {
        if (!mEventObservers.contains(observer)) {
            mEventObservers.add(observer);
        }
    }

    /**
     * 解除注册
     *
     * @param observer
     */
    public void unregisterBluetooth(BluetoothObserver observer) {
        if (mEventObservers.contains(observer)) {
            mEventObservers.remove(observer);
        }
    }

    /**
     * 通知
     *
     * @param action
     */
    void notifyData(String action) {
        int size = mEventObservers.size();
        for (int i = 0, len = size; i < len; i++) {
            mEventObservers.get(i).update(action);
        }
    }

    /**
     * 通知
     *
     * @param characteristic
     */
    void notifyData(BluetoothGattCharacteristic characteristic) {
        int size = mEventObservers.size();
        for (int i = 0, len = size; i < len; i++) {
            mEventObservers.get(i).update(ACTION_DATA_AVAILABLE, characteristic);
        }
    }
}
