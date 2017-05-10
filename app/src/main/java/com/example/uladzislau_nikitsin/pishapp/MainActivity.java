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
import android.widget.TextView;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import java.net.URI;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "Pish";
    private TextView outputTextView;
    private Button startSearchButton;
    private Button registerButtton;
    private BrowseRegistryListener registryListener;
    private AndroidUpnpService upnpBrowseService;
    private AndroidUpnpService upnpRegistrationService;

    private ServiceConnection registrationService = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpRegistrationService = (AndroidUpnpService) service;
                try {
                    LocalDevice binaryLightDevice = createDevice();
                    upnpRegistrationService.getRegistry().addDevice(binaryLightDevice);
                } catch (Exception ex) {
                    Log.d(TAG, "Creating BinaryLight device failed", ex);
                    return;
                }
        }
        public void onServiceDisconnected(ComponentName className) {
            upnpBrowseService = null;
        }
    };

    private ServiceConnection browseService = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpBrowseService = (AndroidUpnpService) service;
            for (Device device : upnpBrowseService.getRegistry().getDevices()) {
                outputTextView.setText(""+device.getDisplayString());
            }
            outputTextView.setText("");
            upnpBrowseService.getRegistry().addListener(registryListener);
            upnpBrowseService.getControlPoint().search();
        }
        public void onServiceDisconnected(ComponentName className) {
            upnpBrowseService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                new FixedAndroidLogHandler()
        );

        registryListener = initListener();

        outputTextView = (TextView) findViewById(R.id.text_view_main3_output);
        outputTextView.setText("");

        startSearchButton = (Button) findViewById(R.id.button_main3_start_search);
        startSearchButton.setOnClickListener(this);

        registerButtton = (Button) findViewById(R.id.button_main3_register);
        registerButtton.setOnClickListener(this);

        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                browseService,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (upnpBrowseService != null) {
            upnpBrowseService.getRegistry().removeListener(registryListener);
        }
        getApplicationContext().unbindService(browseService);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_main3_start_search:
                if (upnpBrowseService == null)
                    break;
                upnpBrowseService.getRegistry().removeAllRemoteDevices();
                upnpBrowseService.getControlPoint().search();
                break;

            case R.id.button_main3_register:
                getApplicationContext().bindService(
                        new Intent(this, AndroidUpnpServiceImpl.class),
                        registrationService,
                        Context.BIND_AUTO_CREATE
                );
            break;
        }
    }

    private LocalDevice createDevice() throws ValidationException, LocalServiceBindingException {
        final UDN udn = new UDN(UUID.randomUUID());
        final DeviceType type = new UDADeviceType("BinaryLight", 1);
        final DeviceDetails details = new DeviceDetails(
                "Friendly Binary Light",
                new ManufacturerDetails("ACME"),
                new ModelDetails("AndroidLight", "A light with on/off switch.", "v1"),
                createDeviceURI());
        return new LocalDevice(new DeviceIdentity(udn),type,details,(LocalService)null);
    }

    private URI createDeviceURI() {
        final String ip = Utills.getIp();
        final int port = Utills.getPort();
        return URI.create("http://pish@"+ip+":"+port+"/pish?pish#pish)");
    }

    private BrowseRegistryListener initListener() {
         return new BrowseRegistryListener() {
            @Override
            public void showMessage(final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        outputTextView.setText(outputTextView.getText() + message +"\n");
                    }
                });
            }
        };
    }
}