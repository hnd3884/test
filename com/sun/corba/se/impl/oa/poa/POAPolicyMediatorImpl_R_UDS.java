package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;

public class POAPolicyMediatorImpl_R_UDS extends POAPolicyMediatorBase_R
{
    private Servant defaultServant;
    
    POAPolicyMediatorImpl_R_UDS(final Policies policies, final POAImpl poaImpl) {
        super(policies, poaImpl);
        this.defaultServant = null;
        if (!policies.useDefaultServant()) {
            throw poaImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
        }
    }
    
    @Override
    protected Object internalGetServant(final byte[] array, final String s) throws ForwardRequest {
        Servant servant = this.internalIdToServant(array);
        if (servant == null) {
            servant = this.defaultServant;
        }
        if (servant == null) {
            throw this.poa.invocationWrapper().poaNoDefaultServant();
        }
        return servant;
    }
    
    @Override
    public void etherealizeAll() {
    }
    
    @Override
    public ServantManager getServantManager() throws WrongPolicy {
        throw new WrongPolicy();
    }
    
    @Override
    public void setServantManager(final ServantManager servantManager) throws WrongPolicy {
        throw new WrongPolicy();
    }
    
    @Override
    public Servant getDefaultServant() throws NoServant, WrongPolicy {
        if (this.defaultServant == null) {
            throw new NoServant();
        }
        return this.defaultServant;
    }
    
    @Override
    public void setDefaultServant(final Servant defaultServant) throws WrongPolicy {
        this.setDelegate(this.defaultServant = defaultServant, "DefaultServant".getBytes());
    }
    
    @Override
    public Servant idToServant(final byte[] array) throws WrongPolicy, ObjectNotActive {
        Servant servant = this.internalKeyToServant(new ActiveObjectMap.Key(array));
        if (servant == null && this.defaultServant != null) {
            servant = this.defaultServant;
        }
        if (servant == null) {
            throw new ObjectNotActive();
        }
        return servant;
    }
}
