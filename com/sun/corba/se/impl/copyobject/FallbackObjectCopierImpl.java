package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;
import com.sun.corba.se.spi.copyobject.ObjectCopier;

public class FallbackObjectCopierImpl implements ObjectCopier
{
    private ObjectCopier first;
    private ObjectCopier second;
    
    public FallbackObjectCopierImpl(final ObjectCopier first, final ObjectCopier second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public Object copy(final Object o) throws ReflectiveCopyException {
        try {
            return this.first.copy(o);
        }
        catch (final ReflectiveCopyException ex) {
            return this.second.copy(o);
        }
    }
}
