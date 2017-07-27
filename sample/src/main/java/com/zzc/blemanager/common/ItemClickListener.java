package com.zzc.blemanager.common;

/**
 * Created : zzc
 * Time : 2017/7/26
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public interface ItemClickListener<M> {
    void itemClick(int position, M model);

    boolean itemLongClick(int position, M model);
}
