package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.spi.oa.OAInvocationInfo;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;

public class InfoOnlyServantCacheLocalCRDImpl extends ServantCacheLocalCRDBase
{
    public InfoOnlyServantCacheLocalCRDImpl(final ORB orb, final int n, final IOR ior) {
        super(orb, n, ior);
    }
    
    @Override
    public ServantObject servant_preinvoke(final org.omg.CORBA.Object object, final String s, final Class clazz) {
        final OAInvocationInfo cachedInfo = this.getCachedInfo();
        if (!this.checkForCompatibleServant(cachedInfo, clazz)) {
            return null;
        }
        final OAInvocationInfo oaInvocationInfo = new OAInvocationInfo(cachedInfo, s);
        this.orb.pushInvocationInfo(oaInvocationInfo);
        return oaInvocationInfo;
    }
    
    @Override
    public void servant_postinvoke(final org.omg.CORBA.Object object, final ServantObject servantObject) {
        this.orb.popInvocationInfo();
    }
}
