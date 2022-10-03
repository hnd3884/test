package com.oracle.webservices.internal.api.databinding;

import java.util.HashMap;
import java.util.Map;
import com.sun.xml.internal.ws.api.ServiceSharedFeatureMarker;
import javax.xml.ws.WebServiceFeature;

public class DatabindingModeFeature extends WebServiceFeature implements ServiceSharedFeatureMarker
{
    public static final String ID = "http://jax-ws.java.net/features/databinding";
    public static final String GLASSFISH_JAXB = "glassfish.jaxb";
    private String mode;
    private Map<String, Object> properties;
    
    public DatabindingModeFeature(final String mode) {
        this.mode = mode;
        this.properties = new HashMap<String, Object>();
    }
    
    public String getMode() {
        return this.mode;
    }
    
    @Override
    public String getID() {
        return "http://jax-ws.java.net/features/databinding";
    }
    
    public Map<String, Object> getProperties() {
        return this.properties;
    }
    
    public static Builder builder() {
        return new Builder(new DatabindingModeFeature(null));
    }
    
    public static final class Builder
    {
        private final DatabindingModeFeature o;
        
        Builder(final DatabindingModeFeature x) {
            this.o = x;
        }
        
        public DatabindingModeFeature build() {
            return this.o;
        }
        
        public Builder value(final String x) {
            this.o.mode = x;
            return this;
        }
    }
}
