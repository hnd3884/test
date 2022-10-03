package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.OADestroyed;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.oa.OAInvocationInfo;

public abstract class ServantCacheLocalCRDBase extends LocalClientRequestDispatcherBase
{
    private OAInvocationInfo cachedInfo;
    protected POASystemException wrapper;
    
    protected ServantCacheLocalCRDBase(final ORB orb, final int n, final IOR ior) {
        super(orb, n, ior);
        this.wrapper = POASystemException.get(orb, "rpc.protocol");
    }
    
    protected synchronized OAInvocationInfo getCachedInfo() {
        if (!this.servantIsLocal) {
            throw this.wrapper.servantMustBeLocal();
        }
        if (this.cachedInfo == null) {
            final ObjectAdapter find = this.oaf.find(this.oaid);
            this.cachedInfo = find.makeInvocationInfo(this.objectId);
            this.orb.pushInvocationInfo(this.cachedInfo);
            try {
                find.enter();
                find.getInvocationServant(this.cachedInfo);
            }
            catch (final ForwardException ex) {
                throw this.wrapper.illegalForwardRequest(ex);
            }
            catch (final OADestroyed oaDestroyed) {
                throw this.wrapper.adapterDestroyed(oaDestroyed);
            }
            finally {
                find.returnServant();
                find.exit();
                this.orb.popInvocationInfo();
            }
        }
        return this.cachedInfo;
    }
}
