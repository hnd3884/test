package org.glassfish.hk2.api;

import org.glassfish.hk2.internal.ServiceLocatorFactoryImpl;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;

public abstract class ServiceLocatorFactory
{
    private static ServiceLocatorFactory INSTANCE;
    
    public static ServiceLocatorFactory getInstance() {
        return ServiceLocatorFactory.INSTANCE;
    }
    
    public abstract ServiceLocator create(final String p0);
    
    public abstract ServiceLocator create(final String p0, final ServiceLocator p1);
    
    public abstract ServiceLocator create(final String p0, final ServiceLocator p1, final ServiceLocatorGenerator p2);
    
    public abstract ServiceLocator create(final String p0, final ServiceLocator p1, final ServiceLocatorGenerator p2, final CreatePolicy p3);
    
    public abstract ServiceLocator find(final String p0);
    
    public abstract void destroy(final String p0);
    
    public abstract void destroy(final ServiceLocator p0);
    
    public abstract void addListener(final ServiceLocatorListener p0);
    
    public abstract void removeListener(final ServiceLocatorListener p0);
    
    static {
        ServiceLocatorFactory.INSTANCE = new ServiceLocatorFactoryImpl();
    }
    
    public enum CreatePolicy
    {
        RETURN, 
        DESTROY, 
        ERROR;
    }
}
