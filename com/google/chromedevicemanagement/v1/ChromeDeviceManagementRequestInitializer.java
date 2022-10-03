package com.google.chromedevicemanagement.v1;

import java.io.IOException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;

public class ChromeDeviceManagementRequestInitializer extends CommonGoogleJsonClientRequestInitializer
{
    public ChromeDeviceManagementRequestInitializer() {
    }
    
    public ChromeDeviceManagementRequestInitializer(final String s) {
        super(s);
    }
    
    public ChromeDeviceManagementRequestInitializer(final String s, final String s2) {
        super(s, s2);
    }
    
    public final void initializeJsonRequest(final AbstractGoogleJsonClientRequest<?> abstractGoogleJsonClientRequest) throws IOException {
        super.initializeJsonRequest((AbstractGoogleJsonClientRequest)abstractGoogleJsonClientRequest);
        this.initializeChromeDeviceManagementRequest((ChromeDeviceManagementRequest<?>)abstractGoogleJsonClientRequest);
    }
    
    protected void initializeChromeDeviceManagementRequest(final ChromeDeviceManagementRequest<?> chromeDeviceManagementRequest) throws IOException {
    }
}
