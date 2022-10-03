package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import sun.corba.OutputStreamFactory;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import java.nio.ByteBuffer;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;

public abstract class CorbaContactInfoBase implements CorbaContactInfo
{
    protected ORB orb;
    protected CorbaContactInfoList contactInfoList;
    protected IOR effectiveTargetIOR;
    protected short addressingDisposition;
    protected OutboundConnectionCache connectionCache;
    
    @Override
    public Broker getBroker() {
        return this.orb;
    }
    
    @Override
    public ContactInfoList getContactInfoList() {
        return this.contactInfoList;
    }
    
    @Override
    public ClientRequestDispatcher getClientRequestDispatcher() {
        return this.orb.getRequestDispatcherRegistry().getClientRequestDispatcher(this.getEffectiveProfile().getObjectKeyTemplate().getSubcontractId());
    }
    
    @Override
    public void setConnectionCache(final OutboundConnectionCache connectionCache) {
        this.connectionCache = connectionCache;
    }
    
    @Override
    public OutboundConnectionCache getConnectionCache() {
        return this.connectionCache;
    }
    
    @Override
    public MessageMediator createMessageMediator(final Broker broker, final ContactInfo contactInfo, final Connection connection, final String s, final boolean b) {
        return new CorbaMessageMediatorImpl((ORB)broker, contactInfo, connection, GIOPVersion.chooseRequestVersion((ORB)broker, this.effectiveTargetIOR), this.effectiveTargetIOR, ((CorbaConnection)connection).getNextRequestId(), this.getAddressingDisposition(), s, b);
    }
    
    @Override
    public MessageMediator createMessageMediator(final Broker broker, final Connection connection) {
        final ORB orb = (ORB)broker;
        final CorbaConnection corbaConnection = (CorbaConnection)connection;
        if (orb.transportDebugFlag) {
            if (corbaConnection.shouldReadGiopHeaderOnly()) {
                this.dprint(".createMessageMediator: waiting for message header on connection: " + corbaConnection);
            }
            else {
                this.dprint(".createMessageMediator: waiting for message on connection: " + corbaConnection);
            }
        }
        MessageBase messageBase;
        if (corbaConnection.shouldReadGiopHeaderOnly()) {
            messageBase = MessageBase.readGIOPHeader(orb, corbaConnection);
        }
        else {
            messageBase = MessageBase.readGIOPMessage(orb, corbaConnection);
        }
        final ByteBuffer byteBuffer = messageBase.getByteBuffer();
        messageBase.setByteBuffer(null);
        return new CorbaMessageMediatorImpl(orb, corbaConnection, messageBase, byteBuffer);
    }
    
    @Override
    public MessageMediator finishCreatingMessageMediator(final Broker broker, final Connection connection, final MessageMediator messageMediator) {
        final ORB orb = (ORB)broker;
        final CorbaConnection corbaConnection = (CorbaConnection)connection;
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        if (orb.transportDebugFlag) {
            this.dprint(".finishCreatingMessageMediator: waiting for message body on connection: " + corbaConnection);
        }
        final Message dispatchHeader = corbaMessageMediator.getDispatchHeader();
        dispatchHeader.setByteBuffer(corbaMessageMediator.getDispatchBuffer());
        final Message giopBody = MessageBase.readGIOPBody(orb, corbaConnection, dispatchHeader);
        final ByteBuffer byteBuffer = giopBody.getByteBuffer();
        giopBody.setByteBuffer(null);
        corbaMessageMediator.setDispatchHeader(giopBody);
        corbaMessageMediator.setDispatchBuffer(byteBuffer);
        return corbaMessageMediator;
    }
    
    @Override
    public OutputObject createOutputObject(final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        final CDROutputObject cdrOutputObject = OutputStreamFactory.newCDROutputObject(this.orb, messageMediator, corbaMessageMediator.getRequestHeader(), corbaMessageMediator.getStreamFormatVersion());
        messageMediator.setOutputObject(cdrOutputObject);
        return cdrOutputObject;
    }
    
    @Override
    public InputObject createInputObject(final Broker broker, final MessageMediator messageMediator) {
        final CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)messageMediator;
        return new CDRInputObject((ORB)broker, (CorbaConnection)messageMediator.getConnection(), corbaMessageMediator.getDispatchBuffer(), corbaMessageMediator.getDispatchHeader());
    }
    
    @Override
    public short getAddressingDisposition() {
        return this.addressingDisposition;
    }
    
    @Override
    public void setAddressingDisposition(final short addressingDisposition) {
        this.addressingDisposition = addressingDisposition;
    }
    
    @Override
    public IOR getTargetIOR() {
        return this.contactInfoList.getTargetIOR();
    }
    
    @Override
    public IOR getEffectiveTargetIOR() {
        return this.effectiveTargetIOR;
    }
    
    @Override
    public IIOPProfile getEffectiveProfile() {
        return this.effectiveTargetIOR.getProfile();
    }
    
    @Override
    public String toString() {
        return "CorbaContactInfoBase[]";
    }
    
    protected void dprint(final String s) {
        ORBUtility.dprint("CorbaContactInfoBase", s);
    }
}
