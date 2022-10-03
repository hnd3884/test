package com.sun.xml.internal.ws.runtime.config;

import java.util.List;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class TubelineFeature extends WebServiceFeature
{
    public static final String ID = "com.sun.xml.internal.ws.runtime.config.TubelineFeature";
    
    @FeatureConstructor({ "enabled" })
    public TubelineFeature(final boolean enabled) {
        super.enabled = enabled;
    }
    
    @ManagedAttribute
    @Override
    public String getID() {
        return "com.sun.xml.internal.ws.runtime.config.TubelineFeature";
    }
    
    List<String> getTubeFactories() {
        return null;
    }
}
