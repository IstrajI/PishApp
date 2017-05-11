package com.example.uladzislau_nikitsin.pishapp;

import android.util.Log;

import org.fourthline.cling.model.meta.Device;

public class DeviceModel {
    private String UUID;
    private String name;
    private String ip;
    private String port;

    DeviceModel(){}
    DeviceModel(final Device realDevice) {
        this.setUUID(String.valueOf(realDevice.getIdentity().getUdn()));
        this.setName(realDevice.getDisplayString());
        Log.d("device name", ""+realDevice.getIdentity().getUdn());
        Log.d("device details", "" + realDevice.getDetails());
        Log.d("presentation" , ""+realDevice.getDetails().getPresentationURI());
        this.setIp(realDevice.getDetails().getPresentationURI().getHost());
        this.setPort(String.valueOf(realDevice.getDetails().getPresentationURI().getPort()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
