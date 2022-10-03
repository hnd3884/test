package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.impl.protocol.POALocalCRDImpl;
import com.sun.corba.se.impl.protocol.JIDLLocalCRDImpl;
import com.sun.corba.se.impl.protocol.FullServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.InfoOnlyServantCacheLocalCRDImpl;
import com.sun.corba.se.impl.protocol.MinimalServantCacheLocalCRDImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.impl.protocol.INSServerRequestDispatcher;
import com.sun.corba.se.impl.protocol.BootstrapServerRequestDispatcher;
import com.sun.corba.se.impl.protocol.CorbaServerRequestDispatcherImpl;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.protocol.CorbaClientRequestDispatcherImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;

public final class RequestDispatcherDefault
{
    private RequestDispatcherDefault() {
    }
    
    public static ClientRequestDispatcher makeClientRequestDispatcher() {
        return new CorbaClientRequestDispatcherImpl();
    }
    
    public static CorbaServerRequestDispatcher makeServerRequestDispatcher(final ORB orb) {
        return new CorbaServerRequestDispatcherImpl(orb);
    }
    
    public static CorbaServerRequestDispatcher makeBootstrapServerRequestDispatcher(final ORB orb) {
        return new BootstrapServerRequestDispatcher(orb);
    }
    
    public static CorbaServerRequestDispatcher makeINSServerRequestDispatcher(final ORB orb) {
        return new INSServerRequestDispatcher(orb);
    }
    
    public static LocalClientRequestDispatcherFactory makeMinimalServantCacheLocalClientRequestDispatcherFactory(final ORB orb) {
        return new LocalClientRequestDispatcherFactory() {
            @Override
            public LocalClientRequestDispatcher create(final int n, final IOR ior) {
                return new MinimalServantCacheLocalCRDImpl(orb, n, ior);
            }
        };
    }
    
    public static LocalClientRequestDispatcherFactory makeInfoOnlyServantCacheLocalClientRequestDispatcherFactory(final ORB orb) {
        return new LocalClientRequestDispatcherFactory() {
            @Override
            public LocalClientRequestDispatcher create(final int n, final IOR ior) {
                return new InfoOnlyServantCacheLocalCRDImpl(orb, n, ior);
            }
        };
    }
    
    public static LocalClientRequestDispatcherFactory makeFullServantCacheLocalClientRequestDispatcherFactory(final ORB orb) {
        return new LocalClientRequestDispatcherFactory() {
            @Override
            public LocalClientRequestDispatcher create(final int n, final IOR ior) {
                return new FullServantCacheLocalCRDImpl(orb, n, ior);
            }
        };
    }
    
    public static LocalClientRequestDispatcherFactory makeJIDLLocalClientRequestDispatcherFactory(final ORB orb) {
        return new LocalClientRequestDispatcherFactory() {
            @Override
            public LocalClientRequestDispatcher create(final int n, final IOR ior) {
                return new JIDLLocalCRDImpl(orb, n, ior);
            }
        };
    }
    
    public static LocalClientRequestDispatcherFactory makePOALocalClientRequestDispatcherFactory(final ORB orb) {
        return new LocalClientRequestDispatcherFactory() {
            @Override
            public LocalClientRequestDispatcher create(final int n, final IOR ior) {
                return new POALocalCRDImpl(orb, n, ior);
            }
        };
    }
}
