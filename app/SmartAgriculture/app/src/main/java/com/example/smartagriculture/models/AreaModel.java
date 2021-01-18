package com.example.smartagriculture.models;

public class AreaModel {
    String name;
    String deviceType;
    String deviceId;

    public AreaModel(String name, String deviceType, String deviceId) {
        this.name = name;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
