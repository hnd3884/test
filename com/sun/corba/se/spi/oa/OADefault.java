package com.sun.corba.se.spi.oa;

import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.corba.se.impl.oa.poa.POAFactory;
import com.sun.corba.se.spi.orb.ORB;

public class OADefault
{
    public static ObjectAdapterFactory makePOAFactory(final ORB orb) {
        final POAFactory poaFactory = new POAFactory();
        poaFactory.init(orb);
        return poaFactory;
    }
    
    public static ObjectAdapterFactory makeTOAFactory(final ORB orb) {
        final TOAFactory toaFactory = new TOAFactory();
        toaFactory.init(orb);
        return toaFactory;
    }
}
