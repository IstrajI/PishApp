package com.example.uladzislau_nikitsin.pishapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "pish";
    private Button b1;
    private Button b2;

    private BrowseRegistryListener registryListener = new BrowseRegistryListener();

    private AndroidUpnpService upnpService;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;

            Log.d("pish", "onConnected");
            Toast.makeText(getApplicationContext(),"onConnected", Toast.LENGTH_SHORT).show();

            Toast.makeText(getApplicationContext(),"r listeners: " +upnpService.getRegistry().getListeners().size(), Toast.LENGTH_SHORT).show();
            upnpService.getRegistry().addListener(registryListener);
            Toast.makeText(getApplicationContext(),"r listeners: " +upnpService.getRegistry().getListeners().size(), Toast.LENGTH_SHORT).show();
            upnpService.getRegistry().getListeners();

            Log.d(TAG, "start search");
            Toast.makeText(getApplicationContext(),"start search", Toast.LENGTH_SHORT).show();

            upnpService.getControlPoint().search(50000);

            Log.d(TAG, "finished search");
            Toast.makeText(getApplicationContext(),"finished search", Toast.LENGTH_SHORT).show();


        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fix the logging integration between java.util.logging and Android internal logging
        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                new FixedAndroidLogHandler()
        );
        // Now you can enable logging as needed for various categories of Cling:
        Logger.getLogger("org.fourthline.cling").setLevel(Level.FINEST);


        setContentView(R.layout.activity_pish);

        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (upnpService != null) {
            upnpService.getRegistry().removeListener(registryListener);
        }
        getApplicationContext().unbindService(serviceConnection);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b1:
                if (upnpService == null)
                    break;
                Toast.makeText(this, "searching lan", Toast.LENGTH_SHORT).show();
                upnpService.getRegistry().removeAllRemoteDevices();


                upnpService.getControlPoint().search();
                break;
            case R.id.b2:
                if (upnpService != null) {
                    Router router = upnpService.get().getRouter();
                    try {
                        if (router.isEnabled()) {
                            Toast.makeText(this, "disable router", Toast.LENGTH_SHORT).show();
                            router.disable();
                        } else {
                            Toast.makeText(this, "enable router", Toast.LENGTH_SHORT).show();
                            router.enable();
                        }
                    } catch (RouterException ex) {
                        Toast.makeText(this, "error" + ex.toString(), Toast.LENGTH_LONG).show();
                        ex.printStackTrace(System.err);
                    }
                }
                break;
        }
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