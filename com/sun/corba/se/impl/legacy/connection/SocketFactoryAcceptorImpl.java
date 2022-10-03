package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;

public class SocketFactoryAcceptorImpl extends SocketOrChannelAcceptorImpl
{
    public SocketFactoryAcceptorImpl(final ORB orb, final int n, final String s, final String s2) {
        super(orb, n, s, s2);
    }
    
    @Override
    public boolean initialize() {
        if (this.initialized) {
            return false;
        }
        if (this.orb.transportDebugFlag) {
            this.dprint("initialize: " + this);
        }
        try {
            this.serverSocket = this.orb.getORBData().getLegacySocketFactory().createServerSocket(this.type, this.port);
            this.internalInitialize();
        }
        catch (final Throwable t) {
            throw this.wrapper.createListenerFailed(t, Integer.toString(this.port));
        }
        return this.initialized = true;
    }
    
    @Override
    protected String toStringName() {
        return "SocketFactoryAcceptorImpl";
    }
    
    @Override
    protected void dprint(final String s) {
        ORBUtility.dprint(this.toStringName(), s);
    }
}
