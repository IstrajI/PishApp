package com.example.uladzislau_nikitsin.pishapp;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;

/**
 * Created by Uladzislau_Nikitsin on 24.05.2017.
 */

public class BrouserUpnp extends AndroidUpnpServiceImpl {

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {

            @Override
            public UpnpHeaders getEventSubscriptionHeaders(RemoteService service) {

                UpnpHeaders headers = new UpnpHeaders();
                headers.add("HostName", "Windows-Phone");
                return headers;
            }
            // DOC:SERVICE_TYPE

            @Override
            public UpnpHeaders getDescriptorRetrievalHeaders(RemoteDeviceIdentity identity) {
                UpnpHeaders headers = new UpnpHeaders();
                headers.add("HostName", "Windows-Phone");
                return headers;
            }
        };
    }
}