package com.zzc.blemanager.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzc.blemanager.common.ItemClickListener;
import com.zzc.blemanager.databinding.ItemScanBinding;

import java.util.List;

/**
 * Created : zzc
 * Time : 2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {
    private int itemLayoutId;
    private List<BluetoothDevice> mDevices;
    private LayoutInflater mInflater;
    private ItemClickListener<BluetoothDevice> mItemClickListener;

    public ScanResultAdapter(List<BluetoothDevice> devices, Context context, int itemLayoutId) {
        this.itemLayoutId = itemLayoutId;
        mDevices = devices;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setItemClickListener(ItemClickListener<BluetoothDevice> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(itemLayoutId, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public int getItemCount() {
        return mDevices != null ? mDevices.size() : 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BluetoothDevice device = mDevices.get(position);
        holder.binding.setDevice(device);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.itemClick(position, mDevices.get(position));
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemScanBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
