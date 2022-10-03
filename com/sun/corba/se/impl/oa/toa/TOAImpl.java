package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import org.omg.CORBA.portable.Delegate;
import com.sun.corba.se.impl.protocol.JIDLLocalCRDImpl;
import com.sun.corba.se.pept.protocol.ClientDelegate;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.oa.OADestroyed;
import org.omg.CORBA.Policy;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.impl.oa.NullServantImpl;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import org.omg.CORBA.Object;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.ior.JIDLObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.oa.ObjectAdapterBase;

public class TOAImpl extends ObjectAdapterBase implements TOA
{
    private TransientObjectManager servants;
    
    public TOAImpl(final ORB orb, final TransientObjectManager servants, final String s) {
        super(orb);
        this.servants = servants;
        final JIDLObjectKeyTemplate jidlObjectKeyTemplate = new JIDLObjectKeyTemplate(orb, 2, this.getORB().getTransientServerId());
        this.initializeTemplate(jidlObjectKeyTemplate, true, Policies.defaultPolicies, s, null, jidlObjectKeyTemplate.getObjectAdapterId());
    }
    
    public ObjectCopierFactory getObjectCopierFactory() {
        return this.getORB().getCopierManager().getDefaultObjectCopierFactory();
    }
    
    @Override
    public Object getLocalServant(final byte[] array) {
        return (Object)this.servants.lookupServant(array);
    }
    
    @Override
    public void getInvocationServant(final OAInvocationInfo oaInvocationInfo) {
        java.lang.Object lookupServant = this.servants.lookupServant(oaInvocationInfo.id());
        if (lookupServant == null) {
            lookupServant = new NullServantImpl(this.lifecycleWrapper().nullServant());
        }
        oaInvocationInfo.setServant(lookupServant);
    }
    
    @Override
    public void returnServant() {
    }
    
    @Override
    public String[] getInterfaces(final java.lang.Object o, final byte[] array) {
        return StubAdapter.getTypeIds(o);
    }
    
    @Override
    public Policy getEffectivePolicy(final int n) {
        return null;
    }
    
    @Override
    public int getManagerId() {
        return -1;
    }
    
    @Override
    public short getState() {
        return 1;
    }
    
    @Override
    public void enter() throws OADestroyed {
    }
    
    @Override
    public void exit() {
    }
    
    @Override
    public void connect(final Object servant) {
        final Delegate delegate = StubAdapter.getDelegate(this.getCurrentFactory().make_object(StubAdapter.getTypeIds(servant)[0], this.servants.storeServant(servant, null)));
        final LocalClientRequestDispatcher localClientRequestDispatcher = ((CorbaContactInfoList)((ClientDelegate)delegate).getContactInfoList()).getLocalClientRequestDispatcher();
        if (localClientRequestDispatcher instanceof JIDLLocalCRDImpl) {
            ((JIDLLocalCRDImpl)localClientRequestDispatcher).setServant(servant);
            StubAdapter.setDelegate(servant, delegate);
            return;
        }
        throw new RuntimeException("TOAImpl.connect can not be called on " + localClientRequestDispatcher);
    }
    
    @Override
    public void disconnect(final Object object) {
        final LocalClientRequestDispatcher localClientRequestDispatcher = ((CorbaContactInfoList)((ClientDelegate)StubAdapter.getDelegate(object)).getContactInfoList()).getLocalClientRequestDispatcher();
        if (localClientRequestDispatcher instanceof JIDLLocalCRDImpl) {
            final JIDLLocalCRDImpl jidlLocalCRDImpl = (JIDLLocalCRDImpl)localClientRequestDispatcher;
            this.servants.deleteServant(jidlLocalCRDImpl.getObjectId());
            jidlLocalCRDImpl.unexport();
            return;
        }
        throw new RuntimeException("TOAImpl.disconnect can not be called on " + localClientRequestDispatcher);
    }
}
