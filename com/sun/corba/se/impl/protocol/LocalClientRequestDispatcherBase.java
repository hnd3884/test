package com.sun.corba.se.impl.protocol;

import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;

public abstract class LocalClientRequestDispatcherBase implements LocalClientRequestDispatcher
{
    protected ORB orb;
    int scid;
    protected boolean servantIsLocal;
    protected ObjectAdapterFactory oaf;
    protected ObjectAdapterId oaid;
    protected byte[] objectId;
    private static final ThreadLocal isNextCallValid;
    
    protected LocalClientRequestDispatcherBase(final ORB orb, final int n, final IOR ior) {
        this.orb = orb;
        final IIOPProfile profile = ior.getProfile();
        this.servantIsLocal = (orb.getORBData().isLocalOptimizationAllowed() && profile.isLocal());
        final ObjectKeyTemplate objectKeyTemplate = profile.getObjectKeyTemplate();
        this.scid = objectKeyTemplate.getSubcontractId();
        this.oaf = orb.getRequestDispatcherRegistry().getObjectAdapterFactory(n);
        this.oaid = objectKeyTemplate.getObjectAdapterId();
        this.objectId = profile.getObjectId().getId();
    }
    
    public byte[] getObjectId() {
        return this.objectId;
    }
    
    @Override
    public boolean is_local(final org.omg.CORBA.Object object) {
        return false;
    }
    
    @Override
    public boolean useLocalInvocation(final org.omg.CORBA.Object object) {
        if (LocalClientRequestDispatcherBase.isNextCallValid.get() == Boolean.TRUE) {
            return this.servantIsLocal;
        }
        LocalClientRequestDispatcherBase.isNextCallValid.set(Boolean.TRUE);
        return false;
    }
    
    protected boolean checkForCompatibleServant(final ServantObject servantObject, final Class clazz) {
        if (servantObject == null) {
            return false;
        }
        if (!clazz.isInstance(servantObject.servant)) {
            LocalClientRequestDispatcherBase.isNextCallValid.set(Boolean.FALSE);
            return false;
        }
        return true;
    }
    
    static {
        isNextCallValid = new ThreadLocal() {
            @Override
            protected synchronized Object initialValue() {
                return Boolean.TRUE;
            }
        };
    }
}
