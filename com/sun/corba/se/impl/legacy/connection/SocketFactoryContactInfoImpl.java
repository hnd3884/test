package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.transport.SocketOrChannelContactInfoImpl;

public class SocketFactoryContactInfoImpl extends SocketOrChannelContactInfoImpl
{
    protected ORBUtilSystemException wrapper;
    protected SocketInfo socketInfo;
    
    public SocketFactoryContactInfoImpl() {
    }
    
    public SocketFactoryContactInfoImpl(final ORB orb, final CorbaContactInfoList list, final IOR effectiveTargetIOR, final short addressingDisposition, final SocketInfo socketInfo) {
        super(orb, list);
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.addressingDisposition = addressingDisposition;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.transport");
        this.socketInfo = orb.getORBData().getLegacySocketFactory().getEndPointInfo(orb, effectiveTargetIOR, socketInfo);
        this.socketType = this.socketInfo.getType();
        this.hostname = this.socketInfo.getHost();
        this.port = this.socketInfo.getPort();
    }
    
    @Override
    public Connection createConnection() {
        return new SocketFactoryConnectionImpl(this.orb, this, this.orb.getORBData().connectionSocketUseSelectThreadToWait(), this.orb.getORBData().connectionSocketUseWorkerThreadForEvent());
    }
    
    @Override
    public String toString() {
        return "SocketFactoryContactInfoImpl[" + this.socketType + " " + this.hostname + " " + this.port + "]";
    }
}
