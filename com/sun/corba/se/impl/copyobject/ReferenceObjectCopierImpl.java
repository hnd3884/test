package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.spi.copyobject.ObjectCopier;

public class ReferenceObjectCopierImpl implements ObjectCopier
{
    @Override
    public Object copy(final Object o) {
        return o;
    }
}
