package com.zzc.blemanager.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.zzc.blemanager.R;
import com.zzc.blemanager.adapter.CharacterAdapter;
import com.zzc.blemanager.adapter.LogAdapter;
import com.zzc.blemanager.common.Constants;
import com.zzc.blemanager.common.ItemClickListener;
import com.zzc.blemanager.databinding.ActivitySecondBinding;
import com.zzc.blemanager.databinding.DialogSendBinding;
import com.zzc.blemanager.model.CharacterModel;
import com.zzc.blemanager.model.LogModel;
import com.zzc.blemanager.util.ConvertUtils;
import com.zzc.lib.ble.BleManager;
import com.zzc.lib.ble.BluetoothObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created : zzc
 * Time : 2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class SecondActivity extends AppCompatActivity implements BluetoothObserver {
    public static final String TAG = "SecondActivity";

    private ActivitySecondBinding mBinding;
    private BleManager mBleManager;

    private ArrayList<LogModel> mLogList;
    private LogAdapter mLogAdapter;
    private CharacterAdapter mCharacterAdapter;
    private ArrayList<CharacterModel> mCharacterList;
    private AlertDialog mDialog;
    private DialogSendBinding mDialogSendBinding;
    private String mAddress;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second);
        BluetoothDevice device = getIntent().getParcelableExtra(Constants.INTENT_KEY_PARCEL);
        mBleManager = BleManager.getInstance(this);
        mAddress = device.getAddress();
        mBleManager.connect(mAddress);
        mLogList = new ArrayList<>();
        mLogAdapter = new LogAdapter(mLogList, R.layout.item_log, this);
        mBinding.rvLog.setAdapter(mLogAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        mBinding.rvLog.setLayoutManager(mLayoutManager);
        mBleManager.registerBluetooth(this);
        String connectResult = "正在连接";
        mLogList.add(new LogModel("unknow", connectResult));
        mLogAdapter.notifyDataSetChanged();

        mCharacterList = new ArrayList<>();
        mCharacterAdapter = new CharacterAdapter(mCharacterList, this, R.layout.item_character);
        mBinding.rvCharacter.setAdapter(mCharacterAdapter);
        mCharacterAdapter.setItemClickListener(new ItemClickListener<CharacterModel>() {
            @Override
            public void itemClick(int position, CharacterModel model) {
                send(model);
            }

            @Override
            public boolean itemLongClick(int position, final CharacterModel model) {
                Snackbar.make(mBinding.rvCharacter, "连接Service？", BaseTransientBottomBar.LENGTH_SHORT).setAction("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBleManager.disconnect();
                        mBleManager.setServiceUUIDHead(model.getServiceUUID());
                        mBleManager.connect(mAddress);
                    }
                }).show();
                return true;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<BluetoothGattService> services = mBleManager.getServices();
                for (int i = 0; i < services.size(); i++) {
                    BluetoothGattService bluetoothGattService = services.get(i);
                    String serviceUUID = bluetoothGattService.getUuid().toString();
                    List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
                    for (int j = 0; j < characteristics.size(); j++) {
                        BluetoothGattCharacteristic bluetoothGattCharacteristic = characteristics.get(j);
                        String uuid = bluetoothGattCharacteristic.getUuid().toString();
                        CharacterModel model = new CharacterModel(serviceUUID, uuid);
                        mCharacterList.add(model);
                        mCharacterAdapter.notifyItemInserted(mCharacterList.size());
                    }
                }
            }
        }, 3000);
    }

    private void send(final CharacterModel model) {
        if (mDialog == null) {
            View contentView = getLayoutInflater().inflate(R.layout.dialog_send, null);
            mDialogSendBinding = DataBindingUtil.bind(contentView);
            mDialog = new AlertDialog.Builder(this)
                    .setView(contentView)
                    .create();
        }
        mDialogSendBinding.edtContent.setText("");
        mDialogSendBinding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = mDialogSendBinding.edtContent.getText().toString().trim();
                UUID service = UUID.fromString(model.getServiceUUID());
                UUID character = UUID.fromString(model.getUuid());
                mBleManager.sendData(ConvertUtils.hexString2Bytes(content), service, character);
                mDialog.hide();
            }
        });
        mDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.unbind();
        if (mDialogSendBinding != null) {
            mDialogSendBinding.unbind();
        }
        mBleManager.disconnect();
    }

    @Override
    public void update(final String action) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLogList.add(new LogModel("unknow", action));
                mLogAdapter.notifyDataSetChanged();
                mLayoutManager.scrollToPosition(mLogAdapter.getItemCount());
            }
        });
    }

    @Override
    public void update(String src, BluetoothGattCharacteristic characteristic) {
        byte[] value = characteristic.getValue();
        String uuid = characteristic.getUuid().toString();
        String content = ConvertUtils.bytes2HexString(value);
        final LogModel model = new LogModel(uuid, content);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLogList.add(model);
                mLogAdapter.notifyDataSetChanged();
                mLayoutManager.scrollToPosition(mLogAdapter.getItemCount());
            }
        });
    }
}
