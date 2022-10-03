package com.sun.corba.se.spi.oa;

import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.POASystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.LocalObject;

public abstract class ObjectAdapterBase extends LocalObject implements ObjectAdapter
{
    private ORB orb;
    private final POASystemException _iorWrapper;
    private final POASystemException _invocationWrapper;
    private final POASystemException _lifecycleWrapper;
    private final OMGSystemException _omgInvocationWrapper;
    private final OMGSystemException _omgLifecycleWrapper;
    private IORTemplate iortemp;
    private byte[] adapterId;
    private ObjectReferenceTemplate adapterTemplate;
    private ObjectReferenceFactory currentFactory;
    
    public ObjectAdapterBase(final ORB orb) {
        this.orb = orb;
        this._iorWrapper = POASystemException.get(orb, "oa.ior");
        this._lifecycleWrapper = POASystemException.get(orb, "oa.lifecycle");
        this._omgLifecycleWrapper = OMGSystemException.get(orb, "oa.lifecycle");
        this._invocationWrapper = POASystemException.get(orb, "oa.invocation");
        this._omgInvocationWrapper = OMGSystemException.get(orb, "oa.invocation");
    }
    
    public final POASystemException iorWrapper() {
        return this._iorWrapper;
    }
    
    public final POASystemException lifecycleWrapper() {
        return this._lifecycleWrapper;
    }
    
    public final OMGSystemException omgLifecycleWrapper() {
        return this._omgLifecycleWrapper;
    }
    
    public final POASystemException invocationWrapper() {
        return this._invocationWrapper;
    }
    
    public final OMGSystemException omgInvocationWrapper() {
        return this._omgInvocationWrapper;
    }
    
    public final void initializeTemplate(final ObjectKeyTemplate objectKeyTemplate, final boolean b, final Policies policies, final String s, final String s2, final ObjectAdapterId objectAdapterId) {
        this.adapterId = objectKeyTemplate.getAdapterId();
        this.iortemp = IORFactories.makeIORTemplate(objectKeyTemplate);
        this.orb.getCorbaTransportManager().addToIORTemplate(this.iortemp, policies, s, s2, objectAdapterId);
        this.adapterTemplate = IORFactories.makeObjectReferenceTemplate(this.orb, this.iortemp);
        this.currentFactory = this.adapterTemplate;
        if (b) {
            final PIHandler piHandler = this.orb.getPIHandler();
            if (piHandler != null) {
                piHandler.objectAdapterCreated(this);
            }
        }
        this.iortemp.makeImmutable();
    }
    
    public final Object makeObject(final String s, final byte[] array) {
        return this.currentFactory.make_object(s, array);
    }
    
    public final byte[] getAdapterId() {
        return this.adapterId;
    }
    
    @Override
    public final ORB getORB() {
        return this.orb;
    }
    
    @Override
    public abstract Policy getEffectivePolicy(final int p0);
    
    @Override
    public final IORTemplate getIORTemplate() {
        return this.iortemp;
    }
    
    @Override
    public abstract int getManagerId();
    
    @Override
    public abstract short getState();
    
    @Override
    public final ObjectReferenceTemplate getAdapterTemplate() {
        return this.adapterTemplate;
    }
    
    @Override
    public final ObjectReferenceFactory getCurrentFactory() {
        return this.currentFactory;
    }
    
    @Override
    public final void setCurrentFactory(final ObjectReferenceFactory currentFactory) {
        this.currentFactory = currentFactory;
    }
    
    @Override
    public abstract Object getLocalServant(final byte[] p0);
    
    @Override
    public abstract void getInvocationServant(final OAInvocationInfo p0);
    
    @Override
    public abstract void returnServant();
    
    @Override
    public abstract void enter() throws OADestroyed;
    
    @Override
    public abstract void exit();
    
    protected abstract ObjectCopierFactory getObjectCopierFactory();
    
    @Override
    public OAInvocationInfo makeInvocationInfo(final byte[] array) {
        final OAInvocationInfo oaInvocationInfo = new OAInvocationInfo(this, array);
        oaInvocationInfo.setCopierFactory(this.getObjectCopierFactory());
        return oaInvocationInfo;
    }
    
    @Override
    public abstract String[] getInterfaces(final java.lang.Object p0, final byte[] p1);
}
