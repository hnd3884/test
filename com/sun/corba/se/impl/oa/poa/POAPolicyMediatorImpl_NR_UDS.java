package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.Servant;

public class POAPolicyMediatorImpl_NR_UDS extends POAPolicyMediatorBase
{
    private Servant defaultServant;
    
    POAPolicyMediatorImpl_NR_UDS(final Policies policies, final POAImpl poaImpl) {
        super(policies, poaImpl);
        if (policies.retainServants()) {
            throw poaImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
        }
        if (!policies.useDefaultServant()) {
            throw poaImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
        }
        this.defaultServant = null;
    }
    
    @Override
    protected Object internalGetServant(final byte[] array, final String s) throws ForwardRequest {
        if (this.defaultServant == null) {
            throw this.poa.invocationWrapper().poaNoDefaultServant();
        }
        return this.defaultServant;
    }
    
    @Override
    public void returnServant() {
    }
    
    @Override
    public void etherealizeAll() {
    }
    
    @Override
    public void clearAOM() {
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
    public final void activateObject(final byte[] array, final Servant servant) throws WrongPolicy, ServantAlreadyActive, ObjectAlreadyActive {
        throw new WrongPolicy();
    }
    
    @Override
    public Servant deactivateObject(final byte[] array) throws ObjectNotActive, WrongPolicy {
        throw new WrongPolicy();
    }
    
    @Override
    public byte[] servantToId(final Servant servant) throws ServantNotActive, WrongPolicy {
        throw new WrongPolicy();
    }
    
    @Override
    public Servant idToServant(final byte[] array) throws WrongPolicy, ObjectNotActive {
        if (this.defaultServant != null) {
            return this.defaultServant;
        }
        throw new ObjectNotActive();
    }
}
