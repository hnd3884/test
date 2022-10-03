package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.server.DraconianValidationErrorHandler;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class SchemaValidationFeature extends WebServiceFeature
{
    public static final String ID = "http://jax-ws.dev.java.net/features/schema-validation";
    private final Class<? extends ValidationErrorHandler> clazz;
    private final boolean inbound;
    private final boolean outbound;
    
    public SchemaValidationFeature() {
        this(true, true, DraconianValidationErrorHandler.class);
    }
    
    public SchemaValidationFeature(final Class<? extends ValidationErrorHandler> clazz) {
        this(true, true, clazz);
    }
    
    public SchemaValidationFeature(final boolean inbound, final boolean outbound) {
        this(inbound, outbound, DraconianValidationErrorHandler.class);
    }
    
    @FeatureConstructor({ "inbound", "outbound", "handler" })
    public SchemaValidationFeature(final boolean inbound, final boolean outbound, final Class<? extends ValidationErrorHandler> clazz) {
        this.enabled = true;
        this.inbound = inbound;
        this.outbound = outbound;
        this.clazz = clazz;
    }
    
    @ManagedAttribute
    @Override
    public String getID() {
        return "http://jax-ws.dev.java.net/features/schema-validation";
    }
    
    @ManagedAttribute
    public Class<? extends ValidationErrorHandler> getErrorHandler() {
        return this.clazz;
    }
    
    public boolean isInbound() {
        return this.inbound;
    }
    
    public boolean isOutbound() {
        return this.outbound;
    }
}
