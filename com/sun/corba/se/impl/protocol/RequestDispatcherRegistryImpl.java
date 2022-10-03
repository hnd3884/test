package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcherFactory;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import java.util.HashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;

public class RequestDispatcherRegistryImpl implements RequestDispatcherRegistry
{
    private ORB orb;
    protected int defaultId;
    private DenseIntMapImpl SDRegistry;
    private DenseIntMapImpl CSRegistry;
    private DenseIntMapImpl OAFRegistry;
    private DenseIntMapImpl LCSFRegistry;
    private Set objectAdapterFactories;
    private Set objectAdapterFactoriesView;
    private Map stringToServerSubcontract;
    
    public RequestDispatcherRegistryImpl(final ORB orb, final int defaultId) {
        this.orb = orb;
        this.defaultId = defaultId;
        this.SDRegistry = new DenseIntMapImpl();
        this.CSRegistry = new DenseIntMapImpl();
        this.OAFRegistry = new DenseIntMapImpl();
        this.LCSFRegistry = new DenseIntMapImpl();
        this.objectAdapterFactories = new HashSet();
        this.objectAdapterFactoriesView = Collections.unmodifiableSet((Set<?>)this.objectAdapterFactories);
        this.stringToServerSubcontract = new HashMap();
    }
    
    @Override
    public synchronized void registerClientRequestDispatcher(final ClientRequestDispatcher clientRequestDispatcher, final int n) {
        this.CSRegistry.set(n, clientRequestDispatcher);
    }
    
    @Override
    public synchronized void registerLocalClientRequestDispatcherFactory(final LocalClientRequestDispatcherFactory localClientRequestDispatcherFactory, final int n) {
        this.LCSFRegistry.set(n, localClientRequestDispatcherFactory);
    }
    
    @Override
    public synchronized void registerServerRequestDispatcher(final CorbaServerRequestDispatcher corbaServerRequestDispatcher, final int n) {
        this.SDRegistry.set(n, corbaServerRequestDispatcher);
    }
    
    @Override
    public synchronized void registerServerRequestDispatcher(final CorbaServerRequestDispatcher corbaServerRequestDispatcher, final String s) {
        this.stringToServerSubcontract.put(s, corbaServerRequestDispatcher);
    }
    
    @Override
    public synchronized void registerObjectAdapterFactory(final ObjectAdapterFactory objectAdapterFactory, final int n) {
        this.objectAdapterFactories.add(objectAdapterFactory);
        this.OAFRegistry.set(n, objectAdapterFactory);
    }
    
    @Override
    public CorbaServerRequestDispatcher getServerRequestDispatcher(final int n) {
        CorbaServerRequestDispatcher corbaServerRequestDispatcher = (CorbaServerRequestDispatcher)this.SDRegistry.get(n);
        if (corbaServerRequestDispatcher == null) {
            corbaServerRequestDispatcher = (CorbaServerRequestDispatcher)this.SDRegistry.get(this.defaultId);
        }
        return corbaServerRequestDispatcher;
    }
    
    @Override
    public CorbaServerRequestDispatcher getServerRequestDispatcher(final String s) {
        CorbaServerRequestDispatcher corbaServerRequestDispatcher = this.stringToServerSubcontract.get(s);
        if (corbaServerRequestDispatcher == null) {
            corbaServerRequestDispatcher = (CorbaServerRequestDispatcher)this.SDRegistry.get(this.defaultId);
        }
        return corbaServerRequestDispatcher;
    }
    
    @Override
    public LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory(final int n) {
        LocalClientRequestDispatcherFactory localClientRequestDispatcherFactory = (LocalClientRequestDispatcherFactory)this.LCSFRegistry.get(n);
        if (localClientRequestDispatcherFactory == null) {
            localClientRequestDispatcherFactory = (LocalClientRequestDispatcherFactory)this.LCSFRegistry.get(this.defaultId);
        }
        return localClientRequestDispatcherFactory;
    }
    
    @Override
    public ClientRequestDispatcher getClientRequestDispatcher(final int n) {
        ClientRequestDispatcher clientRequestDispatcher = (ClientRequestDispatcher)this.CSRegistry.get(n);
        if (clientRequestDispatcher == null) {
            clientRequestDispatcher = (ClientRequestDispatcher)this.CSRegistry.get(this.defaultId);
        }
        return clientRequestDispatcher;
    }
    
    @Override
    public ObjectAdapterFactory getObjectAdapterFactory(final int n) {
        ObjectAdapterFactory objectAdapterFactory = (ObjectAdapterFactory)this.OAFRegistry.get(n);
        if (objectAdapterFactory == null) {
            objectAdapterFactory = (ObjectAdapterFactory)this.OAFRegistry.get(this.defaultId);
        }
        return objectAdapterFactory;
    }
    
    @Override
    public Set getObjectAdapterFactories() {
        return this.objectAdapterFactoriesView;
    }
}
