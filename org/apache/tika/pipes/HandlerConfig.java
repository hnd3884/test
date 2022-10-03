package org.apache.tika.pipes;

import java.util.Locale;
import java.util.Objects;
import org.apache.tika.sax.BasicContentHandlerFactory;
import java.io.Serializable;

public class HandlerConfig implements Serializable
{
    private static final long serialVersionUID = -3861669115439125268L;
    public static HandlerConfig DEFAULT_HANDLER_CONFIG;
    private BasicContentHandlerFactory.HANDLER_TYPE type;
    int writeLimit;
    int maxEmbeddedResources;
    PARSE_MODE parseMode;
    
    public HandlerConfig(final BasicContentHandlerFactory.HANDLER_TYPE type, final PARSE_MODE parseMode, final int writeLimit, final int maxEmbeddedResources) {
        this.type = BasicContentHandlerFactory.HANDLER_TYPE.TEXT;
        this.writeLimit = -1;
        this.maxEmbeddedResources = -1;
        this.parseMode = PARSE_MODE.RMETA;
        this.type = type;
        this.parseMode = parseMode;
        this.writeLimit = writeLimit;
        this.maxEmbeddedResources = maxEmbeddedResources;
    }
    
    public BasicContentHandlerFactory.HANDLER_TYPE getType() {
        return this.type;
    }
    
    public int getWriteLimit() {
        return this.writeLimit;
    }
    
    public int getMaxEmbeddedResources() {
        return this.maxEmbeddedResources;
    }
    
    public PARSE_MODE getParseMode() {
        return this.parseMode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final HandlerConfig that = (HandlerConfig)o;
        return this.writeLimit == that.writeLimit && this.maxEmbeddedResources == that.maxEmbeddedResources && this.type == that.type && this.parseMode == that.parseMode;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.writeLimit, this.maxEmbeddedResources, this.parseMode);
    }
    
    @Override
    public String toString() {
        return "HandlerConfig{type=" + this.type + ", writeLimit=" + this.writeLimit + ", maxEmbeddedResources=" + this.maxEmbeddedResources + ", mode=" + this.parseMode + '}';
    }
    
    static {
        HandlerConfig.DEFAULT_HANDLER_CONFIG = new HandlerConfig(BasicContentHandlerFactory.HANDLER_TYPE.TEXT, PARSE_MODE.RMETA, -1, -1);
    }
    
    public enum PARSE_MODE
    {
        RMETA, 
        CONCATENATE;
        
        public static PARSE_MODE parseMode(final String modeString) {
            for (final PARSE_MODE m : values()) {
                if (m.name().equalsIgnoreCase(modeString)) {
                    return m;
                }
            }
            final StringBuilder sb = new StringBuilder();
            int i = 0;
            for (final PARSE_MODE j : values()) {
                if (i++ > 0) {
                    sb.append(", ");
                }
                sb.append(j.name().toLowerCase(Locale.US));
            }
            throw new IllegalArgumentException("mode must be one of: (" + (Object)sb + "). I regret I do not understand: " + modeString);
        }
    }
}
