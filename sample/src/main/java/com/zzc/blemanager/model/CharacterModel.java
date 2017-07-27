package com.zzc.blemanager.model;

/**
 * Created : zzc
 * Time : 2017/7/27
 * Email : zzc1259@163.com
 * Description : ${desc}
 */

public class CharacterModel {
    private String serviceUUID;
    private String uuid;

    public CharacterModel() {
    }

    public CharacterModel(String serviceUUID, String uuid) {
        this.serviceUUID = serviceUUID;
        this.uuid = uuid;
    }

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
