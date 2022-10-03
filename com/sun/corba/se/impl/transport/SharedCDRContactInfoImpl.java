package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.impl.protocol.SharedCDRClientRequestDispatcherImpl;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;

public class SharedCDRContactInfoImpl extends CorbaContactInfoBase
{
    private static int requestId;
    protected ORBUtilSystemException wrapper;
    
    public SharedCDRContactInfoImpl(final ORB orb, final CorbaContactInfoList contactInfoList, final IOR effectiveTargetIOR, final short addressingDisposition) {
        this.orb = orb;
        this.contactInfoList = contactInfoList;
        this.effectiveTargetIOR = effectiveTargetIOR;
        this.addressingDisposition = addressingDisposition;
    }
    
    @Override
    public ClientRequestDispatcher getClientRequestDispatcher() {
        return new SharedCDRClientRequestDispatcherImpl();
    }
    
    @Override
    public boolean isConnectionBased() {
        return false;
    }
    
    @Override
    public boolean shouldCacheConnection() {
        return false;
    }
    
    @Override
    public String getConnectionCacheType() {
        throw this.getWrapper().methodShouldNotBeCalled();
    }
    
    @Override
    public Connection createConnection() {
        throw this.getWrapper().methodShouldNotBeCalled();
    }
    
    @Override
    public MessageMediator createMessageMediator(final Broker broker, final ContactInfo contactInfo, final Connection connection, final String s, final boolean b) {
        if (connection != null) {
            throw new RuntimeException("connection is not null");
        }
        return new CorbaMessageMediatorImpl((ORB)broker, contactInfo, null, GIOPVersion.chooseRequestVersion((ORB)broker, this.effectiveTargetIOR), this.effectiveTargetIOR, SharedCDRContactInfoImpl.requestId++, this.getAddressingDisposition(), s, b);
    }
    
    @Override
    public OutputObject createOutputObject(final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        final CDROutputObject cdrOutputObject = OutputStreamFactory.newCDROutputObject(this.orb, messageMediator, corbaMessageMediator.getRequestHeader(), corbaMessageMediator.getStreamFormatVersion(), 0);
        messageMediator.setOutputObject(cdrOutputObject);
        return cdrOutputObject;
    }
    
    @Override
    public String getMonitoringName() {
        throw this.getWrapper().methodShouldNotBeCalled();
    }
    
    @Override
    public String toString() {
        return "SharedCDRContactInfoImpl[]";
    }
    
    protected ORBUtilSystemException getWrapper() {
        if (this.wrapper == null) {
            this.wrapper = ORBUtilSystemException.get(this.orb, "rpc.transport");
        }
        return this.wrapper;
    }
    
    static {
        SharedCDRContactInfoImpl.requestId = 0;
    }
}
