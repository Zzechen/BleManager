package com.zzc.blemanager.model;

/**
 * Created : zzc
 * Time : 2017/7/27
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class LogModel {
    private String uuid;
    private String content;

    public LogModel() {
    }

    public LogModel(String uuid, String content) {
        this.uuid = uuid;
        this.content = content;
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
}
