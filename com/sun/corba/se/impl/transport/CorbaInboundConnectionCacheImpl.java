package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.Connection;
import java.util.ArrayList;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.pept.transport.Acceptor;
import java.util.Collection;
import com.sun.corba.se.pept.transport.InboundConnectionCache;

public class CorbaInboundConnectionCacheImpl extends CorbaConnectionCacheBase implements InboundConnectionCache
{
    protected Collection connectionCache;
    private Acceptor acceptor;
    
    public CorbaInboundConnectionCacheImpl(final ORB orb, final Acceptor acceptor) {
        super(orb, acceptor.getConnectionCacheType(), ((CorbaAcceptor)acceptor).getMonitoringName());
        this.connectionCache = new ArrayList();
        this.acceptor = acceptor;
        if (orb.transportDebugFlag) {
            this.dprint(": " + acceptor);
        }
    }
    
    @Override
    public void close() {
        super.close();
        if (this.orb.transportDebugFlag) {
            this.dprint(".close: " + this.acceptor);
        }
        this.acceptor.close();
    }
    
    @Override
    public Connection get(final Acceptor acceptor) {
        throw this.wrapper.methodShouldNotBeCalled();
    }
    
    @Override
    public Acceptor getAcceptor() {
        return this.acceptor;
    }
    
    @Override
    public void put(final Acceptor acceptor, final Connection connection) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".put: " + acceptor + " " + connection);
        }
        synchronized (this.backingStore()) {
            this.connectionCache.add(connection);
            connection.setConnectionCache(this);
            this.dprintStatistics();
        }
    }
    
    @Override
    public void remove(final Connection connection) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".remove: " + connection);
        }
        synchronized (this.backingStore()) {
            this.connectionCache.remove(connection);
            this.dprintStatistics();
        }
    }
    
    @Override
    public Collection values() {
        return this.connectionCache;
    }
    
    @Override
    protected Object backingStore() {
        return this.connectionCache;
    }
    
    @Override
    protected void registerWithMonitoring() {
        final MonitoredObject rootMonitoredObject = this.orb.getMonitoringManager().getRootMonitoredObject();
        MonitoredObject monitoredObject = rootMonitoredObject.getChild("Connections");
        if (monitoredObject == null) {
            monitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Connections", "Statistics on inbound/outbound connections");
            rootMonitoredObject.addChild(monitoredObject);
        }
        MonitoredObject monitoredObject2 = monitoredObject.getChild("Inbound");
        if (monitoredObject2 == null) {
            monitoredObject2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Inbound", "Statistics on inbound connections");
            monitoredObject.addChild(monitoredObject2);
        }
        MonitoredObject monitoredObject3 = monitoredObject2.getChild(this.getMonitoringName());
        if (monitoredObject3 == null) {
            monitoredObject3 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.getMonitoringName(), "Connection statistics");
            monitoredObject2.addChild(monitoredObject3);
        }
        monitoredObject3.addAttribute(new LongMonitoredAttributeBase("NumberOfConnections", "The total number of connections") {
            @Override
            public Object getValue() {
                return new Long(CorbaInboundConnectionCacheImpl.this.numberOfConnections());
            }
        });
        monitoredObject3.addAttribute(new LongMonitoredAttributeBase("NumberOfIdleConnections", "The number of idle connections") {
            @Override
            public Object getValue() {
                return new Long(CorbaInboundConnectionCacheImpl.this.numberOfIdleConnections());
            }
        });
        monitoredObject3.addAttribute(new LongMonitoredAttributeBase("NumberOfBusyConnections", "The number of busy connections") {
            @Override
            public Object getValue() {
                return new Long(CorbaInboundConnectionCacheImpl.this.numberOfBusyConnections());
            }
        });
    }
    
    @Override
    protected void dprint(final String s) {
        ORBUtility.dprint("CorbaInboundConnectionCacheImpl", s);
    }
}
