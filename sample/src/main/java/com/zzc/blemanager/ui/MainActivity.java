package com.zzc.blemanager.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.zzc.blemanager.common.ItemClickListener;
import com.zzc.blemanager.R;
import com.zzc.blemanager.adapter.ScanResultAdapter;
import com.zzc.blemanager.databinding.ActivityMainBinding;
import com.zzc.blemanager.util.AppUtils;
import com.zzc.lib.ble.AbsBleManager;
import com.zzc.lib.ble.BleManager;
import com.zzc.lib.ble.BleOpenListener;

import java.util.ArrayList;
import java.util.List;

import static com.zzc.blemanager.common.Constants.INTENT_KEY_PARCEL;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_PERMISSION = 0x11;
    private ActivityMainBinding mBinding;
    private BleManager mBleManager;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            if (!mDeviceList.contains(bluetoothDevice)) {
                mDeviceList.add(bluetoothDevice);
                mAdapter.notifyItemInserted(mDeviceList.size());
                mAdapter.notifyItemRangeChanged(mDeviceList.size() - 1, mDeviceList.size());
            }
        }
    };
    private List<BluetoothDevice> mDeviceList;
    private ScanResultAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBleManager = BleManager.getInstance(this);
        if (!mBleManager.isAvailable()) {
            Toast.makeText(this, "系统版本过低，不支持BLE，当前版本:" + Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();
            finish();
        }
        mBleManager.initialize();
        mDeviceList = new ArrayList<>();
        mAdapter = new ScanResultAdapter(mDeviceList, this, R.layout.item_scan);
        mAdapter.setItemClickListener(new ItemClickListener<BluetoothDevice>() {
            @Override
            public void itemClick(int position, BluetoothDevice model) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra(INTENT_KEY_PARCEL, model);
                startActivity(intent);
            }

            @Override
            public boolean itemLongClick(int position, BluetoothDevice model) {
                return false;
            }
        });
        mBinding.rvDevice.setAdapter(mAdapter);
        mBinding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtils.checkPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    mBleManager.open(MainActivity.this, new BleOpenListener() {
                        @Override
                        public void result(boolean open) {
                            if (open) {
                                scan();
                            }
                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, CODE_PERMISSION);
                }
            }
        });
        mBinding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBleManager.bleState() == AbsBleManager.STATE_DISCOVERING) {
                    mBleManager.stopScan();
                    mBinding.pb.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scan();
            } else {
                Toast.makeText(this, "蓝牙定位权限获取失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBleManager.stopScan();
        mBinding.pb.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.unbind();
    }

    private void scan() {
        mDeviceList.clear();
        mAdapter.notifyDataSetChanged();
        mBinding.pb.setVisibility(View.VISIBLE);
        mBleManager.scan(mLeScanCallback, new Runnable() {
            @Override
            public void run() {
                mBinding.pb.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "扫描完成", Toast.LENGTH_SHORT).show();
            }
        }, 5000);
    }
}
