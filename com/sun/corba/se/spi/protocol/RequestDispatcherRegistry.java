package com.sun.corba.se.spi.protocol;

import java.util.Set;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;

public interface RequestDispatcherRegistry
{
    void registerClientRequestDispatcher(final ClientRequestDispatcher p0, final int p1);
    
    ClientRequestDispatcher getClientRequestDispatcher(final int p0);
    
    void registerLocalClientRequestDispatcherFactory(final LocalClientRequestDispatcherFactory p0, final int p1);
    
    LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory(final int p0);
    
    void registerServerRequestDispatcher(final CorbaServerRequestDispatcher p0, final int p1);
    
    CorbaServerRequestDispatcher getServerRequestDispatcher(final int p0);
    
    void registerServerRequestDispatcher(final CorbaServerRequestDispatcher p0, final String p1);
    
    CorbaServerRequestDispatcher getServerRequestDispatcher(final String p0);
    
    void registerObjectAdapterFactory(final ObjectAdapterFactory p0, final int p1);
    
    ObjectAdapterFactory getObjectAdapterFactory(final int p0);
    
    Set<ObjectAdapterFactory> getObjectAdapterFactories();
}
