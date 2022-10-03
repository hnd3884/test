package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.ServantManager;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.PortableServer.Servant;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.oa.NullServantImpl;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.ServantLocator;

public class POAPolicyMediatorImpl_NR_USM extends POAPolicyMediatorBase
{
    private ServantLocator locator;
    
    POAPolicyMediatorImpl_NR_USM(final Policies policies, final POAImpl poaImpl) {
        super(policies, poaImpl);
        if (policies.retainServants()) {
            throw poaImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
        }
        if (!policies.useServantManager()) {
            throw poaImpl.invocationWrapper().policyMediatorBadPolicyInFactory();
        }
        this.locator = null;
    }
    
    @Override
    protected Object internalGetServant(final byte[] array, final String s) throws ForwardRequest {
        if (this.locator == null) {
            throw this.poa.invocationWrapper().poaNoServantManager();
        }
        final CookieHolder cookieHolder = this.orb.peekInvocationInfo().getCookieHolder();
        Object preinvoke;
        try {
            this.poa.unlock();
            preinvoke = this.locator.preinvoke(array, this.poa, s, cookieHolder);
            if (preinvoke == null) {
                preinvoke = new NullServantImpl(this.poa.omgInvocationWrapper().nullServantReturned());
            }
            else {
                this.setDelegate((Servant)preinvoke, array);
            }
        }
        finally {
            this.poa.lock();
        }
        return preinvoke;
    }
    
    @Override
    public void returnServant() {
        final OAInvocationInfo peekInvocationInfo = this.orb.peekInvocationInfo();
        if (this.locator == null) {
            return;
        }
        try {
            this.poa.unlock();
            this.locator.postinvoke(peekInvocationInfo.id(), (POA)peekInvocationInfo.oa(), peekInvocationInfo.getOperation(), peekInvocationInfo.getCookieHolder().value, (Servant)peekInvocationInfo.getServantContainer());
        }
        finally {
            this.poa.lock();
        }
    }
    
    @Override
    public void etherealizeAll() {
    }
    
    @Override
    public void clearAOM() {
    }
    
    @Override
    public ServantManager getServantManager() throws WrongPolicy {
        return this.locator;
    }
    
    @Override
    public void setServantManager(final ServantManager servantManager) throws WrongPolicy {
        if (this.locator != null) {
            throw this.poa.invocationWrapper().servantManagerAlreadySet();
        }
        if (servantManager instanceof ServantLocator) {
            this.locator = (ServantLocator)servantManager;
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
        throw new WrongPolicy();
    }
}
