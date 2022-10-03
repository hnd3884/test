package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.Identifiable;
import org.omg.CORBA_2_3.portable.InputStream;
import com.sun.corba.se.spi.ior.IdentifiableFactory;
import java.util.HashMap;
import com.sun.corba.se.impl.logging.IORSystemException;
import java.util.Map;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;

public abstract class IdentifiableFactoryFinderBase implements IdentifiableFactoryFinder
{
    private ORB orb;
    private Map map;
    protected IORSystemException wrapper;
    
    protected IdentifiableFactoryFinderBase(final ORB orb) {
        this.map = new HashMap();
        this.orb = orb;
        this.wrapper = IORSystemException.get(orb, "oa.ior");
    }
    
    protected IdentifiableFactory getFactory(final int n) {
        return this.map.get(new Integer(n));
    }
    
    public abstract Identifiable handleMissingFactory(final int p0, final InputStream p1);
    
    @Override
    public Identifiable create(final int n, final InputStream inputStream) {
        final IdentifiableFactory factory = this.getFactory(n);
        if (factory != null) {
            return factory.create(inputStream);
        }
        return this.handleMissingFactory(n, inputStream);
    }
    
    @Override
    public void registerFactory(final IdentifiableFactory identifiableFactory) {
        this.map.put(new Integer(identifiableFactory.getId()), identifiableFactory);
    }
}
