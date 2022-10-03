package com.google.api.services.androidenterprise;

import java.io.IOException;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.googleapis.services.json.CommonGoogleJsonClientRequestInitializer;

public class AndroidEnterpriseRequestInitializer extends CommonGoogleJsonClientRequestInitializer
{
    public AndroidEnterpriseRequestInitializer() {
    }
    
    public AndroidEnterpriseRequestInitializer(final String key) {
        super(key);
    }
    
    public AndroidEnterpriseRequestInitializer(final String key, final String userIp) {
        super(key, userIp);
    }
    
    public final void initializeJsonRequest(final AbstractGoogleJsonClientRequest<?> request) throws IOException {
        super.initializeJsonRequest((AbstractGoogleJsonClientRequest)request);
        this.initializeAndroidEnterpriseRequest((AndroidEnterpriseRequest)request);
    }
    
    protected void initializeAndroidEnterpriseRequest(final AndroidEnterpriseRequest<?> request) throws IOException {
    }
}
