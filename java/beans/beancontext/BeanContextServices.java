package java.beans.beancontext;

import java.util.Iterator;
import java.util.TooManyListenersException;

public interface BeanContextServices extends BeanContext, BeanContextServicesListener
{
    boolean addService(final Class p0, final BeanContextServiceProvider p1);
    
    void revokeService(final Class p0, final BeanContextServiceProvider p1, final boolean p2);
    
    boolean hasService(final Class p0);
    
    Object getService(final BeanContextChild p0, final Object p1, final Class p2, final Object p3, final BeanContextServiceRevokedListener p4) throws TooManyListenersException;
    
    void releaseService(final BeanContextChild p0, final Object p1, final Object p2);
    
    Iterator getCurrentServiceClasses();
    
    Iterator getCurrentServiceSelectors(final Class p0);
    
    void addBeanContextServicesListener(final BeanContextServicesListener p0);
    
    void removeBeanContextServicesListener(final BeanContextServicesListener p0);
}
