package com.example.smartagriculture.models;

public class AreaModel {
    int id;
    String name;
    String deviceType;
    String deviceId;
    int userId;

    public AreaModel(int id, String name, String deviceType, String deviceId, int userId) {
        this.id = id;
        this.name = name;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
