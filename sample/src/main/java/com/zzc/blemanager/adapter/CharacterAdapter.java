package com.zzc.blemanager.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzc.blemanager.common.ItemClickListener;
import com.zzc.blemanager.databinding.ItemCharacterBinding;
import com.zzc.blemanager.model.CharacterModel;

import java.util.List;

/**
 * Created : zzc
 * Time : 2017/7/27
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {
    private List<CharacterModel> mList;
    private int layoutId;
    private LayoutInflater mInflater;
    private ItemClickListener<CharacterModel> mItemClickListener;

    public CharacterAdapter(List<CharacterModel> list, Context context, int layoutId) {
        mList = list;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);
    }

    public void setItemClickListener(ItemClickListener<CharacterModel> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(layoutId, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CharacterModel model = mList.get(position);
        holder.binding.setModel(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.itemClick(position, model);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemClickListener != null) {
                    return mItemClickListener.itemLongClick(position, model);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemCharacterBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
