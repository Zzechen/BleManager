package com.zzc.lib.ble;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.zzc.lib.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created : zzc
 * Time : 2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleManager extends AbsBleManager {
    public static final String TAG = "BleManager";

    private volatile static BleManager instance;

    private Context mContext;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private EventCallback mEventCallback;
    private EventDispatcher mEventDispatcher;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private Handler handler = new Handler(Looper.getMainLooper());

    private Runnable mStopScanCallback;


    private int mBleState = STATE_NONE;

    private boolean isInited;

    private BleManager(Context context) {
        this.mContext = context;
    }


    public static BleManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BleManager.class) {
                if (instance == null) {
                    instance = new BleManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            LogUtils.d(TAG, "onConnectionStateChange" + status);
            mEventDispatcher.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            LogUtils.d(TAG, "onServicesDiscovered" + status);
            mEventDispatcher.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            LogUtils.d(TAG, "onCharacteristicRead" + status);
            mEventDispatcher.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            LogUtils.d(TAG, "onCharacteristicWrite" + status);
            mEventDispatcher.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            LogUtils.d(TAG, "onCharacteristicChanged");
            mEventDispatcher.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            LogUtils.d(TAG, "onDescriptorWrite" + status);
            mEventDispatcher.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            LogUtils.i(TAG, "onReadRemoteRssi" + status);
            mEventDispatcher.onReadRemoteRssi(gatt, rssi, status);
        }
    };

    public List<BluetoothGattService> getServices() {
        return mBluetoothGatt != null ? mBluetoothGatt.getServices() : new ArrayList<BluetoothGattService>();
    }

    @Override
    public boolean initialize() {
        if (isInited) {
            return true;
        }
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                LogUtils.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter == null) {
            LogUtils.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        mEventCallback = new DefaultEventCallback();
        mEventDispatcher = new EventDispatcher(this, mEventCallback);
        isInited = true;
        return true;
    }

    @Override
    public boolean isAvailable() {
        return Build.VERSION.SDK_INT >= 18;
    }

    @Override
    public boolean isConnected() {
        return mBleState == STATE_CONNECTED;
    }

    private boolean isOpen() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                LogUtils.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }
        if (mBluetoothAdapter == null) {
            LogUtils.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public int bleState() {
        return mBleState;
    }

    @Override
    public void open(Activity activity, BleOpenListener listener) {
        if (isOpen()) {
            if (listener != null) {
                listener.result(true);
            }
            return;
        }
        BleResultFragment fragment = (BleResultFragment) activity.getFragmentManager().findFragmentByTag("OpenBleResultFragment");
        if (fragment == null) {
            fragment = new BleResultFragment();
            fragment.setResultListener(listener);
            FragmentManager fragmentManager = activity.getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(fragment, "OpenBleResultFragment").commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        fragment.openBle();
    }

    @Override
    public void scan(BluetoothAdapter.LeScanCallback leScanCallback, @Nullable final Runnable stopScan, long duration) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "scan," + "permission Manifest.permission.ACCESS_COARSE_LOCATION not allowed");
            return;
        }
        mLeScanCallback = leScanCallback;
        if (mBluetoothAdapter != null) {
            mStopScanCallback = new Runnable() {
                @Override
                public void run() {
                    stopLeScan();
                    if (stopScan != null) {
                        stopScan.run();
                    }
                }
            };
            if (!mBluetoothAdapter.isDiscovering()) {
                handler.postDelayed(mStopScanCallback, duration);
                startLeScan();
            }
        }
    }

    private void startLeScan() {
        if (mBluetoothAdapter != null) {
            mBleState = STATE_DISCOVERING;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    @Override
    public void stopScan() {
        if (mBluetoothAdapter != null && mLeScanCallback != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        if (mStopScanCallback != null) {
            handler.removeCallbacks(mStopScanCallback);
        }
    }

    private void stopLeScan() {
        if (mBluetoothAdapter != null && mLeScanCallback != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        if (mStopScanCallback != null) {
            handler.removeCallbacks(mStopScanCallback);
        }
    }

    @Override
    public boolean connect(String address) {
        close();
        initialize();
        mBleState = STATE_CONNECTING;
        if (mBluetoothAdapter == null || address == null) {
            LogUtils.w(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            mBleState = STATE_NONE;
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (mBluetoothDeviceAddress != null
                && mBluetoothGatt != null) {
            LogUtils.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mBleState = STATE_CONNECTING;
                return true;
            } else {
                mBleState = STATE_NONE;
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        if (device == null) {
            LogUtils.d(TAG, "没有设备");
            mBleState = STATE_NONE;
            return false;
        }
        mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);
        LogUtils.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mBleState = STATE_CONNECTED;
        return true;
    }

    @Override
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtils.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBleState = STATE_NONE;
        mBluetoothGatt.disconnect();
        close();
    }

    @Override
    void release() {
        disconnect();
        close();
        stopLeScan();
        if (mStopScanCallback != null) {
            handler.removeCallbacks(mStopScanCallback);
        }
    }

    @Override
    public void sendData(byte[] bytes, UUID gattUUID, UUID serviceUUID) {
        if (mBluetoothGatt == null) {
            return;
        }
        BluetoothGattService service = mBluetoothGatt.getService(serviceUUID);
        if (service != null) {
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(gattUUID);
            if (characteristic != null) {
                characteristic.setValue(bytes);
                mBluetoothGatt.writeCharacteristic(characteristic);
            }
        }
    }

    @Override
    void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        LogUtils.d(TAG, "readCharacteristic: " + characteristic.getProperties());
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtils.d(TAG, "BluetoothAdapter为空");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    @Override
    void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            LogUtils.d(TAG, "BluetoothAdapter为空");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
                .fromString(CODE_UUID_CLIENT));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    @Override
    void servicesDiscovered(BluetoothGatt gatt, int status) {
        List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
        if (gattServices == null) {
            return;
        }
        for (BluetoothGattService gattService : gattServices) {
            String servierUUID = gattService.getUuid().toString();
            if (!TextUtils.isEmpty(mUUIDHead)) {
                if (servierUUID.contains(mUUIDHead)) {
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        setCharacteristicNotification(gattCharacteristic, true);
                        readCharacteristic(gattCharacteristic);
                    }
                }
            } else {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    setCharacteristicNotification(gattCharacteristic, true);
                    readCharacteristic(gattCharacteristic);
                }
            }
        }
    }

    @Override
    void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
}
