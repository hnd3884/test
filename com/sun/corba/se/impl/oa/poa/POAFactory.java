package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.portable.Delegate;
import org.omg.CORBA.ORBPackage.InvalidName;
import com.sun.corba.se.spi.orbutil.closure.Closure;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManager;
import java.util.Collection;
import java.util.Iterator;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import java.util.Collections;
import java.util.HashSet;
import java.util.WeakHashMap;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Set;
import java.util.Map;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;

public class POAFactory implements ObjectAdapterFactory
{
    private Map exportedServantsToPOA;
    private Set poaManagers;
    private int poaManagerId;
    private int poaId;
    private POAImpl rootPOA;
    private DelegateImpl delegateImpl;
    private ORB orb;
    private POASystemException wrapper;
    private OMGSystemException omgWrapper;
    private boolean isShuttingDown;
    
    public POASystemException getWrapper() {
        return this.wrapper;
    }
    
    public POAFactory() {
        this.exportedServantsToPOA = new WeakHashMap();
        this.isShuttingDown = false;
        this.poaManagers = Collections.synchronizedSet(new HashSet<Object>(4));
        this.poaManagerId = 0;
        this.poaId = 0;
        this.rootPOA = null;
        this.delegateImpl = null;
        this.orb = null;
    }
    
    public synchronized POA lookupPOA(final Servant servant) {
        return this.exportedServantsToPOA.get(servant);
    }
    
    public synchronized void registerPOAForServant(final POA poa, final Servant servant) {
        this.exportedServantsToPOA.put(servant, poa);
    }
    
    public synchronized void unregisterPOAForServant(final POA poa, final Servant servant) {
        this.exportedServantsToPOA.remove(servant);
    }
    
    @Override
    public void init(final ORB orb) {
        this.orb = orb;
        this.wrapper = POASystemException.get(orb, "oa.lifecycle");
        this.omgWrapper = OMGSystemException.get(orb, "oa.lifecycle");
        this.delegateImpl = new DelegateImpl(orb, this);
        this.registerRootPOA();
        orb.getLocalResolver().register("POACurrent", ClosureFactory.makeConstant(new POACurrent(orb)));
    }
    
    @Override
    public ObjectAdapter find(final ObjectAdapterId objectAdapterId) {
        POA poa;
        try {
            int n = 1;
            final Iterator iterator = objectAdapterId.iterator();
            poa = this.getRootPOA();
            while (iterator.hasNext()) {
                final String s = iterator.next();
                if (n != 0) {
                    if (!s.equals("RootPOA")) {
                        throw this.wrapper.makeFactoryNotPoa(s);
                    }
                    n = 0;
                }
                else {
                    poa = poa.find_POA(s, true);
                }
            }
        }
        catch (final AdapterNonExistent adapterNonExistent) {
            throw this.omgWrapper.noObjectAdaptor(adapterNonExistent);
        }
        catch (final OBJECT_NOT_EXIST object_NOT_EXIST) {
            throw object_NOT_EXIST;
        }
        catch (final TRANSIENT transient1) {
            throw transient1;
        }
        catch (final Exception ex) {
            throw this.wrapper.poaLookupError(ex);
        }
        if (poa == null) {
            throw this.wrapper.poaLookupError();
        }
        return (ObjectAdapter)poa;
    }
    
    @Override
    public void shutdown(final boolean b) {
        Iterator iterator = null;
        synchronized (this) {
            this.isShuttingDown = true;
            iterator = new HashSet(this.poaManagers).iterator();
        }
        while (iterator.hasNext()) {
            try {
                ((POAManager)iterator.next()).deactivate(true, b);
            }
            catch (final AdapterInactive adapterInactive) {}
        }
    }
    
    public synchronized void removePoaManager(final POAManager poaManager) {
        this.poaManagers.remove(poaManager);
    }
    
    public synchronized void addPoaManager(final POAManager poaManager) {
        this.poaManagers.add(poaManager);
    }
    
    public synchronized int newPOAManagerId() {
        return this.poaManagerId++;
    }
    
    public void registerRootPOA() {
        this.orb.getLocalResolver().register("RootPOA", ClosureFactory.makeFuture(new Closure() {
            @Override
            public Object evaluate() {
                return POAImpl.makeRootPOA(POAFactory.this.orb);
            }
        }));
    }
    
    public synchronized POA getRootPOA() {
        if (this.rootPOA == null) {
            if (this.isShuttingDown) {
                throw this.omgWrapper.noObjectAdaptor();
            }
            try {
                this.rootPOA = (POAImpl)this.orb.resolve_initial_references("RootPOA");
            }
            catch (final InvalidName invalidName) {
                throw this.wrapper.cantResolveRootPoa(invalidName);
            }
        }
        return this.rootPOA;
    }
    
    public Delegate getDelegateImpl() {
        return this.delegateImpl;
    }
    
    public synchronized int newPOAId() {
        return this.poaId++;
    }
    
    @Override
    public ORB getORB() {
        return this.orb;
    }
}
