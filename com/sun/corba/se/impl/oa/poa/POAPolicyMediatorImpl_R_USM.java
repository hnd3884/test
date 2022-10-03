package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.ServantManager;
import java.util.Set;
import org.omg.PortableServer.Servant;
import com.sun.corba.se.spi.oa.NullServant;
import org.omg.PortableServer.ForwardRequest;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.oa.NullServantImpl;
import org.omg.PortableServer.POA;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.PortableServer.ServantActivator;

public class POAPolicyMediatorImpl_R_USM extends POAPolicyMediatorBase_R
{
    protected ServantActivator activator;
    
    POAPolicyMediatorImpl_R_USM(final Policies policies, final POAImpl poaImpl) {
        super(policies, poaImpl);
        this.activator = null;
        if (!policies.useServantManager()) {
            throw poaImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
        }
    }
    
    private AOMEntry enterEntry(final ActiveObjectMap.Key key) {
        boolean b;
        AOMEntry value;
        do {
            b = false;
            value = this.activeObjectMap.get(key);
            try {
                value.enter();
            }
            catch (final Exception ex) {
                b = true;
            }
        } while (b);
        return value;
    }
    
    @Override
    protected Object internalGetServant(final byte[] array, final String s) throws ForwardRequest {
        if (this.poa.getDebug()) {
            ORBUtility.dprint(this, "Calling POAPolicyMediatorImpl_R_USM.internalGetServant for poa " + this.poa + " operation=" + s);
        }
        try {
            final ActiveObjectMap.Key key = new ActiveObjectMap.Key(array);
            final AOMEntry enterEntry = this.enterEntry(key);
            Object o = this.activeObjectMap.getServant(enterEntry);
            if (o != null) {
                if (this.poa.getDebug()) {
                    ORBUtility.dprint(this, "internalGetServant: servant already activated");
                }
                return o;
            }
            if (this.activator == null) {
                if (this.poa.getDebug()) {
                    ORBUtility.dprint(this, "internalGetServant: no servant activator in POA");
                }
                enterEntry.incarnateFailure();
                throw this.poa.invocationWrapper().poaNoServantManager();
            }
            try {
                if (this.poa.getDebug()) {
                    ORBUtility.dprint(this, "internalGetServant: upcall to incarnate");
                }
                this.poa.unlock();
                o = this.activator.incarnate(array, this.poa);
                if (o == null) {
                    o = new NullServantImpl(this.poa.omgInvocationWrapper().nullServantReturned());
                }
            }
            catch (final ForwardRequest forwardRequest) {
                if (this.poa.getDebug()) {
                    ORBUtility.dprint(this, "internalGetServant: incarnate threw ForwardRequest");
                }
                throw forwardRequest;
            }
            catch (final SystemException ex) {
                if (this.poa.getDebug()) {
                    ORBUtility.dprint(this, "internalGetServant: incarnate threw SystemException " + ex);
                }
                throw ex;
            }
            catch (final Throwable t) {
                if (this.poa.getDebug()) {
                    ORBUtility.dprint(this, "internalGetServant: incarnate threw Throwable " + t);
                }
                throw this.poa.invocationWrapper().poaServantActivatorLookupFailed(t);
            }
            finally {
                this.poa.lock();
                if (o == null || o instanceof NullServant) {
                    if (this.poa.getDebug()) {
                        ORBUtility.dprint(this, "internalGetServant: incarnate failed");
                    }
                    enterEntry.incarnateFailure();
                }
                else {
                    if (this.isUnique && this.activeObjectMap.contains((Servant)o)) {
                        if (this.poa.getDebug()) {
                            ORBUtility.dprint(this, "internalGetServant: servant already assigned to ID");
                        }
                        enterEntry.incarnateFailure();
                        throw this.poa.invocationWrapper().poaServantNotUnique();
                    }
                    if (this.poa.getDebug()) {
                        ORBUtility.dprint(this, "internalGetServant: incarnate complete");
                    }
                    enterEntry.incarnateComplete();
                    this.activateServant(key, enterEntry, (Servant)o);
                }
            }
            return o;
        }
        finally {
            if (this.poa.getDebug()) {
                ORBUtility.dprint(this, "Exiting POAPolicyMediatorImpl_R_USM.internalGetServant for poa " + this.poa);
            }
        }
    }
    
    @Override
    public void returnServant() {
        this.activeObjectMap.get(new ActiveObjectMap.Key(this.orb.peekInvocationInfo().id())).exit();
    }
    
    @Override
    public void etherealizeAll() {
        if (this.activator != null) {
            final Set keySet = this.activeObjectMap.keySet();
            final ActiveObjectMap.Key[] array = keySet.toArray(new ActiveObjectMap.Key[keySet.size()]);
            for (int i = 0; i < keySet.size(); ++i) {
                final ActiveObjectMap.Key key = array[i];
                final AOMEntry value = this.activeObjectMap.get(key);
                final Servant servant = this.activeObjectMap.getServant(value);
                if (servant != null) {
                    final boolean hasMultipleIDs = this.activeObjectMap.hasMultipleIDs(value);
                    value.startEtherealize(null);
                    try {
                        this.poa.unlock();
                        try {
                            this.activator.etherealize(key.id, this.poa, servant, true, hasMultipleIDs);
                        }
                        catch (final Exception ex) {}
                    }
                    finally {
                        this.poa.lock();
                        value.etherealizeComplete();
                    }
                }
            }
        }
    }
    
    @Override
    public ServantManager getServantManager() throws WrongPolicy {
        return this.activator;
    }
    
    @Override
    public void setServantManager(final ServantManager servantManager) throws WrongPolicy {
        if (this.activator != null) {
            throw this.poa.invocationWrapper().servantManagerAlreadySet();
        }
        if (servantManager instanceof ServantActivator) {
            this.activator = (ServantActivator)servantManager;
            return;
        }
        throw this.poa.invocationWrapper().servantManagerBadType();
    }
    
    @Override
    public Servant getDefaultServant() throws NoServant, WrongPolicy {
        throw new WrongPolicy();
    }
    
    @Override
    public void setDefaultServant(final Servant servant) throws WrongPolicy {
        throw new WrongPolicy();
    }
    
    public void deactivateHelper(final ActiveObjectMap.Key key, final AOMEntry aomEntry, final Servant servant) throws ObjectNotActive, WrongPolicy {
        if (this.activator == null) {
            throw this.poa.invocationWrapper().poaNoServantManager();
        }
        aomEntry.startEtherealize(new Etherealizer(this, key, aomEntry, servant, this.poa.getDebug()));
    }
    
    @Override
    public Servant idToServant(final byte[] array) throws WrongPolicy, ObjectNotActive {
        final Servant servant = this.activeObjectMap.getServant(this.activeObjectMap.get(new ActiveObjectMap.Key(array)));
        if (servant != null) {
            return servant;
        }
        throw new ObjectNotActive();
    }
    
    class Etherealizer extends Thread
    {
        private POAPolicyMediatorImpl_R_USM mediator;
        private ActiveObjectMap.Key key;
        private AOMEntry entry;
        private Servant servant;
        private boolean debug;
        
        public Etherealizer(final POAPolicyMediatorImpl_R_USM mediator, final ActiveObjectMap.Key key, final AOMEntry entry, final Servant servant, final boolean debug) {
            this.mediator = mediator;
            this.key = key;
            this.entry = entry;
            this.servant = servant;
            this.debug = debug;
        }
        
        @Override
        public void run() {
            while (true) {
                if (this.debug) {
                    ORBUtility.dprint(this, "Calling Etherealizer.run on key " + this.key);
                    try {
                        try {
                            this.mediator.activator.etherealize(this.key.id, this.mediator.poa, this.servant, false, this.mediator.activeObjectMap.hasMultipleIDs(this.entry));
                        }
                        catch (final Exception ex) {}
                        try {
                            this.mediator.poa.lock();
                            this.entry.etherealizeComplete();
                            this.mediator.activeObjectMap.remove(this.key);
                            ((POAManagerImpl)this.mediator.poa.the_POAManager()).getFactory().unregisterPOAForServant(this.mediator.poa, this.servant);
                        }
                        finally {
                            this.mediator.poa.unlock();
                        }
                    }
                    finally {
                        if (this.debug) {
                            ORBUtility.dprint(this, "Exiting Etherealizer.run");
                        }
                    }
                    return;
                }
                continue;
            }
        }
    }
}
