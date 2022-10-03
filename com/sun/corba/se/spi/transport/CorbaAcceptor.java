package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.pept.transport.Acceptor;

public interface CorbaAcceptor extends Acceptor
{
    String getObjectAdapterId();
    
    String getObjectAdapterManagerId();
    
    void addToIORTemplate(final IORTemplate p0, final Policies p1, final String p2);
    
    String getMonitoringName();
}
