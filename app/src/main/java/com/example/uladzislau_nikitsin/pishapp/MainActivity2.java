package com.example.uladzislau_nikitsin.pishapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

public class MainActivity2 extends AppCompatActivity {

    private String TAG = "pish";

    private BrowseRegistryListener registryListener = new BrowseRegistryListener();

    private AndroidUpnpService upnpService;


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            upnpService = (AndroidUpnpService) service;
            upnpService.getRegistry().addListener(registryListener);
            upnpService.getControlPoint().search();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            upnpService = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }

        getApplicationContext().unbindService(serviceConnection);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                getApplicationContext().bindService(
                        new Intent(getApplicationContext(), AndroidUpnpServiceImpl.class),
                        serviceConnection,
                        0);
            }
        });

        Button button1 = (Button) findViewById(R.id.stop);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().unbindService(serviceConnection);
            }
        });



    }

    protected class BrowseRegistryListener extends DefaultRegistryListener {



        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            Log.d(TAG, "Discovery started " +device.getDisplayString());
        }

        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
            Log.d(TAG, "Discovery failed" +device.getDisplayString());
        }

        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            Log.d(TAG, "Remote device abailable " +device.getDisplayString());
        }

        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            Log.d(TAG, "Remote device removed " +device.getDisplayString());
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {
            Log.d(TAG, "Local device added " +device.getDisplayString());
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            Log.d(TAG, "Local device removed " +device.getDisplayString());
        }

        @Override
        public void beforeShutdown(Registry registry) {
            Log.d(TAG, "Before shutdown, there was " +registry.getDevices().size() +" devices");
        }

        @Override
        public void afterShutdown() {
            Log.d(TAG, "Shutdown complere");
        }
    }


}
