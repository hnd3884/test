package org.glassfish.hk2.api;

import java.util.List;

public interface ServiceHandle<T>
{
    T getService();
    
    ActiveDescriptor<T> getActiveDescriptor();
    
    boolean isActive();
    
    void destroy();
    
    void setServiceData(final Object p0);
    
    Object getServiceData();
    
    List<ServiceHandle<?>> getSubHandles();
}
