package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.IndexedFilter;

public class SpecificFilterImpl implements IndexedFilter
{
    private final String contract;
    private final String name;
    private final long id;
    private final long locatorId;
    
    public SpecificFilterImpl(final String contract, final String name, final long id, final long locatorId) {
        this.contract = contract;
        this.name = name;
        this.id = id;
        this.locatorId = locatorId;
    }
    
    @Override
    public boolean matches(final Descriptor d) {
        return d.getServiceId() == this.id && d.getLocatorId() == this.locatorId;
    }
    
    @Override
    public String getAdvertisedContract() {
        return this.contract;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
