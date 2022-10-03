package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;

public class StarFilter implements Filter
{
    private static StarFilter INSTANCE;
    
    public static StarFilter getDescriptorFilter() {
        return StarFilter.INSTANCE;
    }
    
    @Override
    public boolean matches(final Descriptor d) {
        return true;
    }
    
    static {
        StarFilter.INSTANCE = new StarFilter();
    }
}
