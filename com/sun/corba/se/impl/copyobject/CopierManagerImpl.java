package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.sun.corba.se.spi.copyobject.CopierManager;

public class CopierManagerImpl implements CopierManager
{
    private int defaultId;
    private DenseIntMapImpl map;
    private ORB orb;
    
    public CopierManagerImpl(final ORB orb) {
        this.defaultId = 0;
        this.map = new DenseIntMapImpl();
        this.orb = orb;
    }
    
    @Override
    public void setDefaultId(final int defaultId) {
        this.defaultId = defaultId;
    }
    
    @Override
    public int getDefaultId() {
        return this.defaultId;
    }
    
    @Override
    public ObjectCopierFactory getObjectCopierFactory(final int n) {
        return (ObjectCopierFactory)this.map.get(n);
    }
    
    @Override
    public ObjectCopierFactory getDefaultObjectCopierFactory() {
        return (ObjectCopierFactory)this.map.get(this.defaultId);
    }
    
    @Override
    public void registerObjectCopierFactory(final ObjectCopierFactory objectCopierFactory, final int n) {
        this.map.set(n, objectCopierFactory);
    }
}
