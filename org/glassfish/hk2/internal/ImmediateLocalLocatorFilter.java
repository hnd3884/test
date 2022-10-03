package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;

public class ImmediateLocalLocatorFilter implements Filter
{
    private final long locatorId;
    
    public ImmediateLocalLocatorFilter(final long locatorId) {
        this.locatorId = locatorId;
    }
    
    @Override
    public boolean matches(final Descriptor d) {
        final String scope = d.getScope();
        return scope != null && d.getLocatorId() == this.locatorId && Immediate.class.getName().equals(scope);
    }
}
