package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketOrChannelContactInfoImpl extends CorbaContactInfoBase implements SocketInfo
{
    protected boolean isHashCodeCached;
    protected int cachedHashCode;
    protected String socketType;
    protected String hostname;
    protected int port;
    
    protected SocketOrChannelContactInfoImpl() {
        this.isHashCodeCached = false;
    }
    
    protected SocketOrChannelContactInfoImpl(final ORB orb, final CorbaContactInfoList contactInfoList) {
        this.isHashCodeCached = false;
        this.orb = orb;
        this.contactInfoList = contactInfoList;
    }
    
    public SocketOrChannelContactInfoImpl(final ORB orb, final CorbaContactInfoList list, final String socketType, final String hostname, final int port) {
        this(orb, list);
        this.socketType = socketType;
        this.hostname = hostname;
        this.port = port;
    }
    
    public SocketOrChannelContactInfoImpl(final ORB orb, final CorbaContactInfoList list, final IOR effectiveTargetIOR, final short addressingDisposition, final String s, final String s2, final int n) {
        this(orb, list, s, s2, n);
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.addressingDisposition = addressingDisposition;
    }
    
    @Override
    public boolean isConnectionBased() {
        return true;
    }
    
    @Override
    public boolean shouldCacheConnection() {
        return true;
    }
    
    @Override
    public String getConnectionCacheType() {
        return "SocketOrChannelConnectionCache";
    }
    
    @Override
    public Connection createConnection() {
        return new SocketOrChannelConnectionImpl(this.orb, this, this.socketType, this.hostname, this.port);
    }
    
    @Override
    public String getMonitoringName() {
        return "SocketConnections";
    }
    
    @Override
    public String getType() {
        return this.socketType;
    }
    
    @Override
    public String getHost() {
        return this.hostname;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    @Override
    public int hashCode() {
        if (!this.isHashCodeCached) {
            this.cachedHashCode = (this.socketType.hashCode() ^ this.hostname.hashCode() ^ this.port);
            this.isHashCodeCached = true;
        }
        return this.cachedHashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof SocketOrChannelContactInfoImpl)) {
            return false;
        }
        final SocketOrChannelContactInfoImpl socketOrChannelContactInfoImpl = (SocketOrChannelContactInfoImpl)o;
        if (this.port != socketOrChannelContactInfoImpl.port) {
            return false;
        }
        if (!this.hostname.equals(socketOrChannelContactInfoImpl.hostname)) {
            return false;
        }
        if (this.socketType == null) {
            if (socketOrChannelContactInfoImpl.socketType != null) {
                return false;
            }
        }
        else if (!this.socketType.equals(socketOrChannelContactInfoImpl.socketType)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "SocketOrChannelContactInfoImpl[" + this.socketType + " " + this.hostname + " " + this.port + "]";
    }
    
    @Override
    protected void dprint(final String s) {
        ORBUtility.dprint("SocketOrChannelContactInfoImpl", s);
    }
}
