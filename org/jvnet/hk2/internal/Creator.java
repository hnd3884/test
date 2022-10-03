package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Injectee;
import java.util.List;

public interface Creator<T>
{
    List<Injectee> getInjectees();
    
    T create(final ServiceHandle<?> p0, final SystemDescriptor<?> p1) throws MultiException;
    
    void dispose(final T p0) throws MultiException;
}
