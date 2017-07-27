package com.zzc.blemanager.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created : zzc
 * Time : 2017/7/27
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class LogModel {
    private static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private String uuid;
    private String content;
    private String time;

    public LogModel() {
    }

    public LogModel(String uuid, String content) {
        this.uuid = uuid;
        this.content = content;
        time = DEFAULT_SDF.format(new Date(System.currentTimeMillis()));
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
