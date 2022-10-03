package org.apache.coyote;

import org.apache.tomcat.util.res.StringManager;

public enum ContinueResponseTiming
{
    IMMEDIATELY("immediately"), 
    ON_REQUEST_BODY_READ("onRead"), 
    ALWAYS("always");
    
    private static final StringManager sm;
    private final String configValue;
    
    public static ContinueResponseTiming fromString(final String value) {
        if (ContinueResponseTiming.IMMEDIATELY.toString().equalsIgnoreCase(value)) {
            return ContinueResponseTiming.IMMEDIATELY;
        }
        if (ContinueResponseTiming.ON_REQUEST_BODY_READ.toString().equalsIgnoreCase(value)) {
            return ContinueResponseTiming.ON_REQUEST_BODY_READ;
        }
        throw new IllegalArgumentException(ContinueResponseTiming.sm.getString("continueResponseTiming.invalid", new Object[] { value }));
    }
    
    private ContinueResponseTiming(final String configValue) {
        this.configValue = configValue;
    }
    
    @Override
    public String toString() {
        return this.configValue;
    }
    
    static {
        sm = StringManager.getManager((Class)ContinueResponseTiming.class);
    }
}
