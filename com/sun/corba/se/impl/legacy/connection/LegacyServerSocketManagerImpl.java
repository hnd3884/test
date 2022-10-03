package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.util.Collection;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import java.util.Iterator;
import org.omg.CORBA.INTERNAL;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;

public class LegacyServerSocketManagerImpl implements LegacyServerSocketManager
{
    protected ORB orb;
    private ORBUtilSystemException wrapper;
    
    public LegacyServerSocketManagerImpl(final ORB orb) {
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.transport");
    }
    
    @Override
    public int legacyGetTransientServerPort(final String s) {
        return this.legacyGetServerPort(s, false);
    }
    
    @Override
    public synchronized int legacyGetPersistentServerPort(final String s) {
        if (this.orb.getORBData().getServerIsORBActivated()) {
            return this.legacyGetServerPort(s, true);
        }
        if (this.orb.getORBData().getPersistentPortInitialized()) {
            return this.orb.getORBData().getPersistentServerPort();
        }
        throw this.wrapper.persistentServerportNotSet(CompletionStatus.COMPLETED_MAYBE);
    }
    
    @Override
    public synchronized int legacyGetTransientOrPersistentServerPort(final String s) {
        return this.legacyGetServerPort(s, this.orb.getORBData().getServerIsORBActivated());
    }
    
    @Override
    public synchronized LegacyServerSocketEndPointInfo legacyGetEndpoint(final String s) {
        final Iterator acceptorIterator = this.getAcceptorIterator();
        while (acceptorIterator.hasNext()) {
            final LegacyServerSocketEndPointInfo cast = this.cast(acceptorIterator.next());
            if (cast != null && s.equals(cast.getName())) {
                return cast;
            }
        }
        throw new INTERNAL("No acceptor for: " + s);
    }
    
    @Override
    public boolean legacyIsLocalServerPort(final int n) {
        final Iterator acceptorIterator = this.getAcceptorIterator();
        while (acceptorIterator.hasNext()) {
            final LegacyServerSocketEndPointInfo cast = this.cast(acceptorIterator.next());
            if (cast != null && cast.getPort() == n) {
                return true;
            }
        }
        return false;
    }
    
    private int legacyGetServerPort(final String s, final boolean b) {
        final Iterator acceptorIterator = this.getAcceptorIterator();
        while (acceptorIterator.hasNext()) {
            final LegacyServerSocketEndPointInfo cast = this.cast(acceptorIterator.next());
            if (cast != null && cast.getType().equals(s)) {
                if (b) {
                    return cast.getLocatorPort();
                }
                return cast.getPort();
            }
        }
        return -1;
    }
    
    private Iterator getAcceptorIterator() {
        final Collection acceptors = this.orb.getCorbaTransportManager().getAcceptors(null, null);
        if (acceptors != null) {
            return acceptors.iterator();
        }
        throw this.wrapper.getServerPortCalledBeforeEndpointsInitialized();
    }
    
    private LegacyServerSocketEndPointInfo cast(final Object o) {
        if (o instanceof LegacyServerSocketEndPointInfo) {
            return (LegacyServerSocketEndPointInfo)o;
        }
        return null;
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("LegacyServerSocketManagerImpl", s);
    }
}
