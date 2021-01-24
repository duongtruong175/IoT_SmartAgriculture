package com.example.smartagriculture.models;

public class DeviceDataModel {
    int id;
    String deviceId;
    Double temperatureAir;
    Double humidityAir;
    Double humiditySoil;
    long timeSend;

    public DeviceDataModel(int id, String deviceId, Double temperatureAir, Double humidityAir, Double humiditySoil, long timeSend) {
        this.id = id;
        this.deviceId = deviceId;
        this.temperatureAir = temperatureAir;
        this.humidityAir = humidityAir;
        this.humiditySoil = humiditySoil;
        this.timeSend = timeSend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getTemperatureAir() {
        return temperatureAir;
    }

    public void setTemperatureAir(Double temperatureAir) {
        this.temperatureAir = temperatureAir;
    }

    public Double getHumidityAir() {
        return humidityAir;
    }

    public void setHumidityAir(Double humidityAir) {
        this.humidityAir = humidityAir;
    }

    public Double getHumiditySoil() {
        return humiditySoil;
    }

    public void setHumiditySoil(Double humiditySoil) {
        this.humiditySoil = humiditySoil;
    }

    public long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(long timeSend) {
        this.timeSend = timeSend;
    }
}
