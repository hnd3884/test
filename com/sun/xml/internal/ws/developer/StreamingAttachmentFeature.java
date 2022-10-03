package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEConfig;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public final class StreamingAttachmentFeature extends WebServiceFeature
{
    public static final String ID = "http://jax-ws.dev.java.net/features/mime";
    private MIMEConfig config;
    private String dir;
    private boolean parseEagerly;
    private long memoryThreshold;
    
    public StreamingAttachmentFeature() {
    }
    
    @FeatureConstructor({ "dir", "parseEagerly", "memoryThreshold" })
    public StreamingAttachmentFeature(@Nullable final String dir, final boolean parseEagerly, final long memoryThreshold) {
        this.enabled = true;
        this.dir = dir;
        this.parseEagerly = parseEagerly;
        this.memoryThreshold = memoryThreshold;
    }
    
    @ManagedAttribute
    @Override
    public String getID() {
        return "http://jax-ws.dev.java.net/features/mime";
    }
    
    @ManagedAttribute
    public MIMEConfig getConfig() {
        if (this.config == null) {
            (this.config = new MIMEConfig()).setDir(this.dir);
            this.config.setParseEagerly(this.parseEagerly);
            this.config.setMemoryThreshold(this.memoryThreshold);
            this.config.validate();
        }
        return this.config;
    }
    
    public void setDir(final String dir) {
        this.dir = dir;
    }
    
    public void setParseEagerly(final boolean parseEagerly) {
        this.parseEagerly = parseEagerly;
    }
    
    public void setMemoryThreshold(final long memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }
}
