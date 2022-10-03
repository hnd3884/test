package com.adventnet.authentication.callback;

import java.io.Serializable;
import javax.security.auth.callback.Callback;

public class ServiceCallback implements Callback, Serializable
{
    private String serviceName;
    
    public ServiceCallback() {
        this.serviceName = null;
    }
    
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
}
