package com.example.smartagriculture.models;

public class DeviceDataModel {
    Double temperatureAir;
    Double humidityAir;
    Double humiditySoil;
    String timeSend;

    public DeviceDataModel(Double temperatureAir, Double humidityAir, Double humiditySoil, String timeSend) {
        this.temperatureAir = temperatureAir;
        this.humidityAir = humidityAir;
        this.humiditySoil = humiditySoil;
        this.timeSend = timeSend;
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

    public String getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(String timeSend) {
        this.timeSend = timeSend;
    }
}
