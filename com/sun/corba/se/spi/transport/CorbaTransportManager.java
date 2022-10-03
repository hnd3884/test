package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.ior.IORTemplate;
import java.util.Collection;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.pept.transport.TransportManager;

public interface CorbaTransportManager extends TransportManager
{
    public static final String SOCKET_OR_CHANNEL_CONNECTION_CACHE = "SocketOrChannelConnectionCache";
    
    Collection getAcceptors(final String p0, final ObjectAdapterId p1);
    
    void addToIORTemplate(final IORTemplate p0, final Policies p1, final String p2, final String p3, final ObjectAdapterId p4);
}
