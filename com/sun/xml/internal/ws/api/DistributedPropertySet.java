package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;

public abstract class DistributedPropertySet extends BaseDistributedPropertySet
{
    @Deprecated
    public void addSatellite(@NotNull final com.sun.xml.internal.ws.api.PropertySet satellite) {
        super.addSatellite(satellite);
    }
    
    @Deprecated
    public void addSatellite(@NotNull final Class keyClass, @NotNull final com.sun.xml.internal.ws.api.PropertySet satellite) {
        super.addSatellite(keyClass, satellite);
    }
    
    @Deprecated
    public void copySatelliteInto(@NotNull final DistributedPropertySet r) {
        super.copySatelliteInto(r);
    }
    
    @Deprecated
    public void removeSatellite(final com.sun.xml.internal.ws.api.PropertySet satellite) {
        super.removeSatellite(satellite);
    }
}
