package com.example.uladzislau_nikitsin.pishapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DevicesAdapter.DevicesClickLister {
    private String TAG = "Pish";
    private Button startSearchButton;
    private Button registerButtton;
    private RecyclerView devicesListRecyclerView;

    private EditText deviceNameEditText;
    public TextView consoleTextView;


    private Server server;
    private BrowseRegistryListener registryListener;
    private AndroidUpnpService upnpBrowseService;
    private AndroidUpnpService upnpRegistrationService;
    private DevicesAdapter devicesAdapter;

    private ServiceConnection registrationService = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpRegistrationService = (AndroidUpnpService) service;
            try {
                final String deviceName = deviceNameEditText.getText().toString();

                LocalDevice binaryLightDevice = createDevice(deviceName);
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

        deviceNameEditText = (EditText) findViewById(R.id.edit_text_main3_name);

        consoleTextView = (TextView) findViewById(R.id.text_view_main3_console);

        startSearchButton = (Button) findViewById(R.id.button_main3_start_search);
        startSearchButton.setOnClickListener(this);

        registerButtton = (Button) findViewById(R.id.button_main3_register);
        registerButtton.setOnClickListener(this);

        devicesListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_main3_devices_list);
        final LinearLayoutManager devicesListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        devicesAdapter = new DevicesAdapter();
        devicesAdapter.setDevicesClickLister(this);
        devicesListRecyclerView.setLayoutManager(devicesListLayoutManager);
        devicesListRecyclerView.setAdapter(devicesAdapter);

        server = new Server(this);
        server.start();

        Log.d("starting_server", "" + Utills.getIp() + ":" + Utills.getPort());


/*        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                browseService,
                Context.BIND_AUTO_CREATE
        );*/
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
        switch (v.getId()) {
            case R.id.button_main3_start_search:
                startService(new Intent(this, AndroidUpnpServiceImpl.class));
                Log.d("search", "" + "pish");
                getApplicationContext().bindService(
                        new Intent(this, AndroidUpnpServiceImpl.class),
                        browseService,
                        Context.BIND_AUTO_CREATE
                );
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

    private LocalDevice createDevice(final String deviceName) throws ValidationException, LocalServiceBindingException {

        final UDN udn = new UDN(UUID.randomUUID());
        final DeviceType type = new UDADeviceType("BinaryLight", 1);
        final DeviceDetails details = new DeviceDetails(
                "Friendly Binary Light",
                new ManufacturerDetails("PISH"),
                new ModelDetails(deviceName, "A light with on/off switch.", "v1"),
                createDeviceURI());
        LocalDevice localDevice = new LocalDevice(new DeviceIdentity(udn), type, details, (LocalService) null);
        Log.d("local_device_created", "" + localDevice.getDetails().getPresentationURI());
        return localDevice;
    }

    private URI createDeviceURI() {
        final String ip = Utills.getIp();
        final int port = Utills.getPort();
        return URI.create("http://pish@" + ip + ":" + port + "/pish?pish#pish)");
    }

    private BrowseRegistryListener initListener() {
        return new BrowseRegistryListener() {
            @Override
            public void showMessage(final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        consoleTextView.setText(consoleTextView.getText() + message + "\n");
                    }
                });
            }

            @Override
            public void addDevice(final Device device) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (device.getDetails().getPresentationURI() == null) {
                            consoleTextView.setText(consoleTextView.getText() + "Cant add device with URI == null \n");
                            return;
                        }
                        devicesAdapter.add(new DeviceModel(device));
                    }
                });
            }

            @Override
            public void removeDevice(final Device device) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (device.getDetails().getPresentationURI() == null) return;
                        devicesAdapter.remove(new DeviceModel(device));
                    }
                });
            }
        };
    }

    @Override
    public void onItemClick(final View view, final int position, final RecyclerView.Adapter adapter) {
        final DeviceModel item = ((DevicesAdapter) adapter).getItemByPosition(position);
        final Client client = new Client(item.getIp(),
                Integer.parseInt(item.getPort()),
                consoleTextView);
        client.execute();
    }
}