package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import java.util.Collection;
import java.util.Iterator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnectionCache;
import com.sun.corba.se.pept.transport.ConnectionCache;

public abstract class CorbaConnectionCacheBase implements ConnectionCache, CorbaConnectionCache
{
    protected ORB orb;
    protected long timestamp;
    protected String cacheType;
    protected String monitoringName;
    protected ORBUtilSystemException wrapper;
    
    protected CorbaConnectionCacheBase(final ORB orb, final String cacheType, final String monitoringName) {
        this.timestamp = 0L;
        this.orb = orb;
        this.cacheType = cacheType;
        this.monitoringName = monitoringName;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.transport");
        this.registerWithMonitoring();
        this.dprintCreation();
    }
    
    @Override
    public String getCacheType() {
        return this.cacheType;
    }
    
    @Override
    public synchronized void stampTime(final Connection connection) {
        connection.setTimeStamp(this.timestamp++);
    }
    
    @Override
    public long numberOfConnections() {
        synchronized (this.backingStore()) {
            return this.values().size();
        }
    }
    
    @Override
    public void close() {
        synchronized (this.backingStore()) {
            final Iterator iterator = this.values().iterator();
            while (iterator.hasNext()) {
                ((CorbaConnection)iterator.next()).closeConnectionResources();
            }
        }
    }
    
    @Override
    public long numberOfIdleConnections() {
        long n = 0L;
        synchronized (this.backingStore()) {
            final Iterator iterator = this.values().iterator();
            while (iterator.hasNext()) {
                if (!((Connection)iterator.next()).isBusy()) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    @Override
    public long numberOfBusyConnections() {
        long n = 0L;
        synchronized (this.backingStore()) {
            final Iterator iterator = this.values().iterator();
            while (iterator.hasNext()) {
                if (((Connection)iterator.next()).isBusy()) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    @Override
    public synchronized boolean reclaim() {
        try {
            final long numberOfConnections = this.numberOfConnections();
            if (this.orb.transportDebugFlag) {
                this.dprint(".reclaim->: " + numberOfConnections + " (" + this.orb.getORBData().getHighWaterMark() + "/" + this.orb.getORBData().getLowWaterMark() + "/" + this.orb.getORBData().getNumberToReclaim() + ")");
            }
            if (numberOfConnections <= this.orb.getORBData().getHighWaterMark() || numberOfConnections < this.orb.getORBData().getLowWaterMark()) {
                return false;
            }
            final Object backingStore = this.backingStore();
            synchronized (backingStore) {
                for (int i = 0; i < this.orb.getORBData().getNumberToReclaim(); ++i) {
                    Connection connection = null;
                    long timeStamp = Long.MAX_VALUE;
                    for (final Connection connection2 : this.values()) {
                        if (!connection2.isBusy() && connection2.getTimeStamp() < timeStamp) {
                            connection = connection2;
                            timeStamp = connection2.getTimeStamp();
                        }
                    }
                    if (connection == null) {
                        return false;
                    }
                    try {
                        if (this.orb.transportDebugFlag) {
                            this.dprint(".reclaim: closing: " + connection);
                        }
                        connection.close();
                    }
                    catch (final Exception ex) {}
                }
                if (this.orb.transportDebugFlag) {
                    this.dprint(".reclaim: connections reclaimed (" + (numberOfConnections - this.numberOfConnections()) + ")");
                }
            }
            return true;
        }
        finally {
            if (this.orb.transportDebugFlag) {
                this.dprint(".reclaim<-: " + this.numberOfConnections());
            }
        }
    }
    
    @Override
    public String getMonitoringName() {
        return this.monitoringName;
    }
    
    public abstract Collection values();
    
    protected abstract Object backingStore();
    
    protected abstract void registerWithMonitoring();
    
    protected void dprintCreation() {
        if (this.orb.transportDebugFlag) {
            this.dprint(".constructor: cacheType: " + this.getCacheType() + " monitoringName: " + this.getMonitoringName());
        }
    }
    
    protected void dprintStatistics() {
        if (this.orb.transportDebugFlag) {
            this.dprint(".stats: " + this.numberOfConnections() + "/total " + this.numberOfBusyConnections() + "/busy " + this.numberOfIdleConnections() + "/idle (" + this.orb.getORBData().getHighWaterMark() + "/" + this.orb.getORBData().getLowWaterMark() + "/" + this.orb.getORBData().getNumberToReclaim() + ")");
        }
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("CorbaConnectionCacheBase", s);
    }
}
