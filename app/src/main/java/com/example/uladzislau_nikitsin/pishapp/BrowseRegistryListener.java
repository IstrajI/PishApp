package com.example.uladzislau_nikitsin.pishapp;

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

abstract class BrowseRegistryListener extends DefaultRegistryListener {
    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
        showMessage("Discovery started " +device.getDisplayString() + " " +device.getDetails().getBaseURL().getPort());
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
        showMessage("Discovery failed " +device.getDisplayString()+ " " +device.getDetails().getBaseURL().getPort());
    }
    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        showMessage("Remote device added " +device.getDisplayString()+ " " +device.getDetails().getBaseURL().getPort());
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        showMessage("Remote device removed " +device.getDisplayString()+ " " +device.getDetails().getPresentationURI().getPort());
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        showMessage("Local device added " +device.getDisplayString()+ " " +device.getDetails().getPresentationURI().getPort());
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        showMessage("Local device removed " +device.getDisplayString()+ " " +device.getDetails().getBaseURL().getPort());
    }

    public abstract void showMessage(final String message);
}