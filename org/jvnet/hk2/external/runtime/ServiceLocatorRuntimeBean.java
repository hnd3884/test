package org.jvnet.hk2.external.runtime;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ServiceLocatorRuntimeBean
{
    int getNumberOfDescriptors();
    
    int getNumberOfChildren();
    
    int getServiceCacheSize();
    
    int getServiceCacheMaximumSize();
    
    void clearServiceCache();
    
    int getReflectionCacheSize();
    
    void clearReflectionCache();
}
