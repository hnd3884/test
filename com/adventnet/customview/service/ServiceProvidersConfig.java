package com.adventnet.customview.service;

import java.io.Serializable;

public class ServiceProvidersConfig implements Serializable
{
    String[] addOnServiceProvidersForServer;
    String[] addOnServiceProvidersForClient;
    
    public String[] getAddOnServiceProvidersForServer() {
        return this.addOnServiceProvidersForServer;
    }
    
    public void setAddOnServiceProvidersForServer(final String[] v) {
        this.addOnServiceProvidersForServer = v;
    }
    
    public String[] getAddOnServiceProvidersForClient() {
        return this.addOnServiceProvidersForClient;
    }
    
    public void setAddOnServiceProvidersForClient(final String[] v) {
        this.addOnServiceProvidersForClient = v;
    }
}
