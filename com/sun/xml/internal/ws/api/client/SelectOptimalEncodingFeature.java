package com.sun.xml.internal.ws.api.client;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class SelectOptimalEncodingFeature extends WebServiceFeature
{
    public static final String ID = "http://java.sun.com/xml/ns/jaxws/client/selectOptimalEncoding";
    
    public SelectOptimalEncodingFeature() {
        this.enabled = true;
    }
    
    @FeatureConstructor({ "enabled" })
    public SelectOptimalEncodingFeature(final boolean enabled) {
        this.enabled = enabled;
    }
    
    @ManagedAttribute
    @Override
    public String getID() {
        return "http://java.sun.com/xml/ns/jaxws/client/selectOptimalEncoding";
    }
}
