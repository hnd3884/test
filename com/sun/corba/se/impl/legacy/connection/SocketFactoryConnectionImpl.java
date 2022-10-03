package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.transport.SocketOrChannelConnectionImpl;

public class SocketFactoryConnectionImpl extends SocketOrChannelConnectionImpl
{
    public SocketFactoryConnectionImpl(final ORB orb, final CorbaContactInfo contactInfo, final boolean b, final boolean b2) {
        super(orb, b, b2);
        this.contactInfo = contactInfo;
        final boolean b3 = !b;
        final SocketInfo socketInfo = ((SocketFactoryContactInfoImpl)contactInfo).socketInfo;
        try {
            this.socket = orb.getORBData().getLegacySocketFactory().createSocket(socketInfo);
            this.socketChannel = this.socket.getChannel();
            if (this.socketChannel != null) {
                this.socketChannel.configureBlocking(b3);
            }
            else {
                this.setUseSelectThreadToWait(false);
            }
            if (orb.transportDebugFlag) {
                this.dprint(".initialize: connection created: " + this.socket);
            }
        }
        catch (final GetEndPointInfoAgainException ex) {
            throw this.wrapper.connectFailure(ex, socketInfo.getType(), socketInfo.getHost(), Integer.toString(socketInfo.getPort()));
        }
        catch (final Exception ex2) {
            throw this.wrapper.connectFailure(ex2, socketInfo.getType(), socketInfo.getHost(), Integer.toString(socketInfo.getPort()));
        }
        this.state = 1;
    }
    
    @Override
    public String toString() {
        synchronized (this.stateEvent) {
            return "SocketFactoryConnectionImpl[ " + ((this.socketChannel == null) ? this.socket.toString() : this.socketChannel.toString()) + " " + this.getStateString(this.state) + " " + this.shouldUseSelectThreadToWait() + " " + this.shouldUseWorkerThreadForEvent() + "]";
        }
    }
    
    @Override
    public void dprint(final String s) {
        ORBUtility.dprint("SocketFactoryConnectionImpl", s);
    }
}
