package com.sun.corba.se.impl.oa.poa;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.portable.Delegate;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ForwardRequest;
import com.sun.corba.se.impl.orbutil.ORBConstants;
import com.sun.corba.se.spi.orb.ORB;

public abstract class POAPolicyMediatorBase implements POAPolicyMediator
{
    protected POAImpl poa;
    protected ORB orb;
    private int sysIdCounter;
    private Policies policies;
    private DelegateImpl delegateImpl;
    private int serverid;
    private int scid;
    protected boolean isImplicit;
    protected boolean isUnique;
    protected boolean isSystemId;
    
    @Override
    public final Policies getPolicies() {
        return this.policies;
    }
    
    @Override
    public final int getScid() {
        return this.scid;
    }
    
    @Override
    public final int getServerId() {
        return this.serverid;
    }
    
    POAPolicyMediatorBase(final Policies policies, final POAImpl poa) {
        if (policies.isSingleThreaded()) {
            throw poa.invocationWrapper().singleThreadNotSupported();
        }
        this.delegateImpl = (DelegateImpl)((POAManagerImpl)poa.the_POAManager()).getFactory().getDelegateImpl();
        this.policies = policies;
        this.poa = poa;
        this.orb = poa.getORB();
        switch (policies.servantCachingLevel()) {
            case 0: {
                this.scid = 32;
                break;
            }
            case 1: {
                this.scid = 36;
                break;
            }
            case 2: {
                this.scid = 40;
                break;
            }
            case 3: {
                this.scid = 44;
                break;
            }
        }
        if (policies.isTransient()) {
            this.serverid = this.orb.getTransientServerId();
        }
        else {
            this.serverid = this.orb.getORBData().getPersistentServerId();
            this.scid = ORBConstants.makePersistent(this.scid);
        }
        this.isImplicit = policies.isImplicitlyActivated();
        this.isUnique = policies.isUniqueIds();
        this.isSystemId = policies.isSystemAssignedIds();
        this.sysIdCounter = 0;
    }
    
    @Override
    public final Object getInvocationServant(final byte[] array, final String s) throws ForwardRequest {
        return this.internalGetServant(array, s);
    }
    
    protected final void setDelegate(final Servant servant, final byte[] array) {
        servant._set_delegate(this.delegateImpl);
    }
    
    @Override
    public synchronized byte[] newSystemId() throws WrongPolicy {
        if (!this.isSystemId) {
            throw new WrongPolicy();
        }
        final byte[] array = new byte[8];
        ORBUtility.intToBytes(++this.sysIdCounter, array, 0);
        ORBUtility.intToBytes(this.poa.getPOAId(), array, 4);
        return array;
    }
    
    protected abstract Object internalGetServant(final byte[] p0, final String p1) throws ForwardRequest;
}
