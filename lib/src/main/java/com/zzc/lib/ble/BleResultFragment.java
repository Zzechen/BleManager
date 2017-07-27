package com.zzc.lib.ble;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;


/**
 * Created : zzc
 * Time : 2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class BleResultFragment extends Fragment {
    private BleOpenListener mListener;
    private static final int REQ_OPEN = 0x0012;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean open = false;
        if (requestCode == REQ_OPEN && resultCode == Activity.RESULT_OK) {
            open = true;
        }
        if (mListener != null) {
            mListener.result(open);
        }
    }

    void openBle() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQ_OPEN);
    }

    void setResultListener(BleOpenListener listener) {
        mListener = listener;
    }
}
