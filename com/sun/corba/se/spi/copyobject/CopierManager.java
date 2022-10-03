package com.sun.corba.se.spi.copyobject;

public interface CopierManager
{
    void setDefaultId(final int p0);
    
    int getDefaultId();
    
    ObjectCopierFactory getObjectCopierFactory(final int p0);
    
    ObjectCopierFactory getDefaultObjectCopierFactory();
    
    void registerObjectCopierFactory(final ObjectCopierFactory p0, final int p1);
}
