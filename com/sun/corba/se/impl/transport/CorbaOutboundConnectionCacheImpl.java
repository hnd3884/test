package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import java.util.Collection;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Hashtable;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;

public class CorbaOutboundConnectionCacheImpl extends CorbaConnectionCacheBase implements OutboundConnectionCache
{
    protected Hashtable connectionCache;
    
    public CorbaOutboundConnectionCacheImpl(final ORB orb, final ContactInfo contactInfo) {
        super(orb, contactInfo.getConnectionCacheType(), ((CorbaContactInfo)contactInfo).getMonitoringName());
        this.connectionCache = new Hashtable();
    }
    
    @Override
    public Connection get(final ContactInfo contactInfo) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".get: " + contactInfo + " " + contactInfo.hashCode());
        }
        synchronized (this.backingStore()) {
            this.dprintStatistics();
            return this.connectionCache.get(contactInfo);
        }
    }
    
    @Override
    public void put(final ContactInfo contactInfo, final Connection connection) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".put: " + contactInfo + " " + contactInfo.hashCode() + " " + connection);
        }
        synchronized (this.backingStore()) {
            this.connectionCache.put(contactInfo, connection);
            connection.setConnectionCache(this);
            this.dprintStatistics();
        }
    }
    
    @Override
    public void remove(final ContactInfo contactInfo) {
        if (this.orb.transportDebugFlag) {
            this.dprint(".remove: " + contactInfo + " " + contactInfo.hashCode());
        }
        synchronized (this.backingStore()) {
            if (contactInfo != null) {
                this.connectionCache.remove(contactInfo);
            }
            this.dprintStatistics();
        }
    }
    
    @Override
    public Collection values() {
        return this.connectionCache.values();
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
        MonitoredObject monitoredObject2 = monitoredObject.getChild("Outbound");
        if (monitoredObject2 == null) {
            monitoredObject2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Outbound", "Statistics on outbound connections");
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
                return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfConnections());
            }
        });
        monitoredObject3.addAttribute(new LongMonitoredAttributeBase("NumberOfIdleConnections", "The number of idle connections") {
            @Override
            public Object getValue() {
                return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfIdleConnections());
            }
        });
        monitoredObject3.addAttribute(new LongMonitoredAttributeBase("NumberOfBusyConnections", "The number of busy connections") {
            @Override
            public Object getValue() {
                return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfBusyConnections());
            }
        });
    }
    
    @Override
    protected void dprint(final String s) {
        ORBUtility.dprint("CorbaOutboundConnectionCacheImpl", s);
    }
}
