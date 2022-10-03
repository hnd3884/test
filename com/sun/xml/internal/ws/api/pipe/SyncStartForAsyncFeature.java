package com.sun.xml.internal.ws.api.pipe;

import javax.xml.ws.WebServiceFeature;

public class SyncStartForAsyncFeature extends WebServiceFeature
{
    public SyncStartForAsyncFeature() {
        this.enabled = true;
    }
    
    @Override
    public String getID() {
        return SyncStartForAsyncFeature.class.getSimpleName();
    }
}
