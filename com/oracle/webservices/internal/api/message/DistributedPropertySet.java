package com.oracle.webservices.internal.api.message;

import java.util.Map;
import com.sun.istack.internal.Nullable;

public interface DistributedPropertySet extends PropertySet
{
    @Nullable
     <T extends PropertySet> T getSatellite(final Class<T> p0);
    
    Map<Class<? extends PropertySet>, PropertySet> getSatellites();
    
    void addSatellite(final PropertySet p0);
    
    void addSatellite(final Class<? extends PropertySet> p0, final PropertySet p1);
    
    void removeSatellite(final PropertySet p0);
    
    void copySatelliteInto(final MessageContext p0);
}
