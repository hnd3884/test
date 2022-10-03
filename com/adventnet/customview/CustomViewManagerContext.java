package com.adventnet.customview;

import java.util.Arrays;
import java.io.Serializable;

public class CustomViewManagerContext implements Serializable
{
    String customViewType;
    int mode;
    int instanceId;
    String[] clientServiceProviders;
    
    public CustomViewManagerContext(final String customViewType, final int mode, final int instanceId, final String[] clientServiceProviders) {
        this.customViewType = customViewType;
        this.mode = mode;
        this.instanceId = instanceId;
        this.clientServiceProviders = clientServiceProviders;
    }
    
    public String getCustomViewType() {
        return this.customViewType;
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public int getInstanceId() {
        return this.instanceId;
    }
    
    public String[] getClientServiceProviders() {
        return this.clientServiceProviders;
    }
    
    @Override
    public String toString() {
        final String clientServiceProvidersStr = (this.clientServiceProviders != null) ? Arrays.asList(this.clientServiceProviders).toString() : "<NO-ClientSPs/>";
        return "CustomViewManagerContext[" + this.customViewType + "," + this.mode + "," + this.instanceId + "," + clientServiceProvidersStr + "]";
    }
}
