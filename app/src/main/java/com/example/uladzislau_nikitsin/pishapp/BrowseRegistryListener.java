package com.example.uladzislau_nikitsin.pishapp;

import android.util.Log;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

abstract class BrowseRegistryListener extends DefaultRegistryListener {
    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
        showMessage("Discovery started ");
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        showMessage("Discovery failed ");
    }
    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        showMessage("Remote device added " +device.getDisplayString());
        addDevice(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {

        showMessage("Remote device removed " +device.getDisplayString()+ " ");
        removeDevice(device);
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        showMessage("Local device added " +device.getDisplayString()+ " ");
        addDevice(device);
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        showMessage("Local device removed " +device.getDisplayString()+ " " +device.getDetails().getBaseURL().getPort());
        removeDevice(device);
    }

    public abstract void showMessage(final String message);
    public abstract void addDevice(final Device device);
    public abstract void removeDevice(final Device device);

}