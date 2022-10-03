package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.ior.IORTemplate;
import java.util.Iterator;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.Acceptor;
import java.util.Collection;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import java.util.HashMap;
import java.util.ArrayList;
import com.sun.corba.se.pept.transport.Selector;
import java.util.Map;
import java.util.List;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaTransportManager;

public class CorbaTransportManagerImpl implements CorbaTransportManager
{
    protected ORB orb;
    protected List acceptors;
    protected Map outboundConnectionCaches;
    protected Map inboundConnectionCaches;
    protected Selector selector;
    
    public CorbaTransportManagerImpl(final ORB orb) {
        this.orb = orb;
        this.acceptors = new ArrayList();
        this.outboundConnectionCaches = new HashMap();
        this.inboundConnectionCaches = new HashMap();
        this.selector = new SelectorImpl(orb);
    }
    
    @Override
    public ByteBufferPool getByteBufferPool(final int n) {
        throw new RuntimeException();
    }
    
    @Override
    public OutboundConnectionCache getOutboundConnectionCache(final ContactInfo contactInfo) {
        synchronized (contactInfo) {
            if (contactInfo.getConnectionCache() == null) {
                OutboundConnectionCache connectionCache = null;
                synchronized (this.outboundConnectionCaches) {
                    connectionCache = this.outboundConnectionCaches.get(contactInfo.getConnectionCacheType());
                    if (connectionCache == null) {
                        connectionCache = new CorbaOutboundConnectionCacheImpl(this.orb, contactInfo);
                        this.outboundConnectionCaches.put(contactInfo.getConnectionCacheType(), connectionCache);
                    }
                }
                contactInfo.setConnectionCache(connectionCache);
            }
            return contactInfo.getConnectionCache();
        }
    }
    
    @Override
    public Collection getOutboundConnectionCaches() {
        return this.outboundConnectionCaches.values();
    }
    
    @Override
    public InboundConnectionCache getInboundConnectionCache(final Acceptor acceptor) {
        synchronized (acceptor) {
            if (acceptor.getConnectionCache() == null) {
                InboundConnectionCache connectionCache = null;
                synchronized (this.inboundConnectionCaches) {
                    connectionCache = this.inboundConnectionCaches.get(acceptor.getConnectionCacheType());
                    if (connectionCache == null) {
                        connectionCache = new CorbaInboundConnectionCacheImpl(this.orb, acceptor);
                        this.inboundConnectionCaches.put(acceptor.getConnectionCacheType(), connectionCache);
                    }
                }
                acceptor.setConnectionCache(connectionCache);
            }
            return acceptor.getConnectionCache();
        }
    }
    
    @Override
    public Collection getInboundConnectionCaches() {
        return this.inboundConnectionCaches.values();
    }
    
    @Override
    public Selector getSelector(final int n) {
        return this.selector;
    }
    
    @Override
    public synchronized void registerAcceptor(final Acceptor acceptor) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".registerAcceptor->: " + acceptor);
        }
        this.acceptors.add(acceptor);
        if (this.orb.transportDebugFlag) {
            this.dprint(".registerAcceptor<-: " + acceptor);
        }
    }
    
    @Override
    public Collection getAcceptors() {
        return this.getAcceptors(null, null);
    }
    
    @Override
    public synchronized void unregisterAcceptor(final Acceptor acceptor) {
        this.acceptors.remove(acceptor);
    }
    
    @Override
    public void close() {
        try {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close->");
            }
            final Iterator iterator = this.outboundConnectionCaches.values().iterator();
            while (iterator.hasNext()) {
                ((ConnectionCache)iterator.next()).close();
            }
            for (final Object next : this.inboundConnectionCaches.values()) {
                ((ConnectionCache)next).close();
                this.unregisterAcceptor(((InboundConnectionCache)next).getAcceptor());
            }
            this.getSelector(0).close();
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".close<-");
            }
        }
    }
    
    @Override
    public Collection getAcceptors(final String s, final ObjectAdapterId objectAdapterId) {
        for (final Acceptor acceptor : this.acceptors) {
            if (acceptor.initialize() && acceptor.shouldRegisterAcceptEvent()) {
                this.orb.getTransportManager().getSelector(0).registerForEvent(acceptor.getEventHandler());
            }
        }
        return this.acceptors;
    }
    
    @Override
    public void addToIORTemplate(final IORTemplate iorTemplate, final Policies policies, final String s, final String s2, final ObjectAdapterId objectAdapterId) {
        final Iterator iterator = this.getAcceptors(s2, objectAdapterId).iterator();
        while (iterator.hasNext()) {
            ((CorbaAcceptor)iterator.next()).addToIORTemplate(iorTemplate, policies, s);
        }
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("CorbaTransportManagerImpl", s);
    }
}
