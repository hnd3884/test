package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ForwardRequest;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.oa.NullServantImpl;

public class POAPolicyMediatorImpl_R_AOM extends POAPolicyMediatorBase_R
{
    POAPolicyMediatorImpl_R_AOM(final Policies policies, final POAImpl poaImpl) {
        super(policies, poaImpl);
        if (!policies.useActiveMapOnly()) {
            throw poaImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
        }
    }
    
    @Override
    protected Object internalGetServant(final byte[] array, final String s) throws ForwardRequest {
        Object internalIdToServant = this.internalIdToServant(array);
        if (internalIdToServant == null) {
            internalIdToServant = new NullServantImpl(this.poa.invocationWrapper().nullServant());
        }
        return internalIdToServant;
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
        throw new WrongPolicy();
    }
    
    @Override
    public void setDefaultServant(final Servant servant) throws WrongPolicy {
        throw new WrongPolicy();
    }
    
    @Override
    public Servant idToServant(final byte[] array) throws WrongPolicy, ObjectNotActive {
        final Servant internalIdToServant = this.internalIdToServant(array);
        if (internalIdToServant == null) {
            throw new ObjectNotActive();
        }
        return internalIdToServant;
    }
}
