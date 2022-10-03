package java.beans.beancontext;

import java.util.Iterator;

public interface BeanContextServiceProvider
{
    Object getService(final BeanContextServices p0, final Object p1, final Class p2, final Object p3);
    
    void releaseService(final BeanContextServices p0, final Object p1, final Object p2);
    
    Iterator getCurrentServiceSelectors(final BeanContextServices p0, final Class p1);
}
