package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import com.sun.corba.se.spi.legacy.interceptor.UnknownType;
import java.util.Iterator;
import org.omg.IOP.TaggedComponent;
import org.omg.CORBA.Policy;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.legacy.interceptor.IORInfoExt;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.CORBA.LocalObject;

public final class IORInfoImpl extends LocalObject implements IORInfo, IORInfoExt
{
    private static final int STATE_INITIAL = 0;
    private static final int STATE_ESTABLISHED = 1;
    private static final int STATE_DONE = 2;
    private int state;
    private ObjectAdapter adapter;
    private ORB orb;
    private ORBUtilSystemException orbutilWrapper;
    private InterceptorsSystemException wrapper;
    private OMGSystemException omgWrapper;
    
    IORInfoImpl(final ObjectAdapter adapter) {
        this.state = 0;
        this.orb = adapter.getORB();
        this.orbutilWrapper = ORBUtilSystemException.get(this.orb, "rpc.protocol");
        this.wrapper = InterceptorsSystemException.get(this.orb, "rpc.protocol");
        this.omgWrapper = OMGSystemException.get(this.orb, "rpc.protocol");
        this.adapter = adapter;
    }
    
    @Override
    public Policy get_effective_policy(final int n) {
        this.checkState(0, 1);
        return this.adapter.getEffectivePolicy(n);
    }
    
    @Override
    public void add_ior_component(final TaggedComponent taggedComponent) {
        this.checkState(0);
        if (taggedComponent == null) {
            this.nullParam();
        }
        this.addIORComponentToProfileInternal(taggedComponent, this.adapter.getIORTemplate().iterator());
    }
    
    @Override
    public void add_ior_component_to_profile(final TaggedComponent taggedComponent, final int n) {
        this.checkState(0);
        if (taggedComponent == null) {
            this.nullParam();
        }
        this.addIORComponentToProfileInternal(taggedComponent, this.adapter.getIORTemplate().iteratorById(n));
    }
    
    @Override
    public int getServerPort(final String s) throws UnknownType {
        this.checkState(0, 1);
        final int legacyGetTransientOrPersistentServerPort = this.orb.getLegacyServerSocketManager().legacyGetTransientOrPersistentServerPort(s);
        if (legacyGetTransientOrPersistentServerPort == -1) {
            throw new UnknownType();
        }
        return legacyGetTransientOrPersistentServerPort;
    }
    
    @Override
    public ObjectAdapter getObjectAdapter() {
        return this.adapter;
    }
    
    @Override
    public int manager_id() {
        this.checkState(0, 1);
        return this.adapter.getManagerId();
    }
    
    @Override
    public short state() {
        this.checkState(0, 1);
        return this.adapter.getState();
    }
    
    @Override
    public ObjectReferenceTemplate adapter_template() {
        this.checkState(1);
        return this.adapter.getAdapterTemplate();
    }
    
    @Override
    public ObjectReferenceFactory current_factory() {
        this.checkState(1);
        return this.adapter.getCurrentFactory();
    }
    
    @Override
    public void current_factory(final ObjectReferenceFactory currentFactory) {
        this.checkState(1);
        this.adapter.setCurrentFactory(currentFactory);
    }
    
    private void addIORComponentToProfileInternal(final TaggedComponent taggedComponent, final Iterator iterator) {
        final com.sun.corba.se.spi.ior.TaggedComponent create = this.orb.getTaggedComponentFactoryFinder().create(this.orb, taggedComponent);
        boolean b = false;
        while (iterator.hasNext()) {
            b = true;
            iterator.next().add(create);
        }
        if (!b) {
            throw this.omgWrapper.invalidProfileId();
        }
    }
    
    private void nullParam() {
        throw this.orbutilWrapper.nullParam();
    }
    
    private void checkState(final int n) {
        if (n != this.state) {
            throw this.wrapper.badState1(new Integer(n), new Integer(this.state));
        }
    }
    
    private void checkState(final int n, final int n2) {
        if (n != this.state && n2 != this.state) {
            throw this.wrapper.badState2(new Integer(n), new Integer(n2), new Integer(this.state));
        }
    }
    
    void makeStateEstablished() {
        this.checkState(0);
        this.state = 1;
    }
    
    void makeStateDone() {
        this.checkState(1);
        this.state = 2;
    }
}
