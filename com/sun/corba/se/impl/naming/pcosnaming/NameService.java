package com.sun.corba.se.impl.naming.pcosnaming;

import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.CORBA.Policy;
import java.io.File;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.NamingContext;

public class NameService
{
    private NamingContext rootContext;
    private POA nsPOA;
    private ServantManagerImpl contextMgr;
    private ORB theorb;
    
    public NameService(final ORB orb, final File file) throws Exception {
        this.rootContext = null;
        this.nsPOA = null;
        this.theorb = orb;
        final POA poa = (POA)orb.resolve_initial_references("RootPOA");
        poa.the_POAManager().activate();
        int n = 0;
        final Policy[] array = new Policy[4];
        array[n++] = poa.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
        array[n++] = poa.create_request_processing_policy(RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
        array[n++] = poa.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID);
        array[n++] = poa.create_servant_retention_policy(ServantRetentionPolicyValue.NON_RETAIN);
        this.nsPOA = poa.create_POA("NameService", null, array);
        this.nsPOA.the_POAManager().activate();
        this.contextMgr = new ServantManagerImpl(orb, file, this);
        final ServantManagerImpl contextMgr = this.contextMgr;
        final String rootObjectKey = ServantManagerImpl.getRootObjectKey();
        final NamingContextImpl addContext = this.contextMgr.addContext(rootObjectKey, new NamingContextImpl(orb, rootObjectKey, this, this.contextMgr));
        addContext.setServantManagerImpl(this.contextMgr);
        addContext.setORB(orb);
        addContext.setRootNameService(this);
        this.nsPOA.set_servant_manager(this.contextMgr);
        this.rootContext = NamingContextHelper.narrow(this.nsPOA.create_reference_with_id(rootObjectKey.getBytes(), NamingContextHelper.id()));
    }
    
    public NamingContext initialNamingContext() {
        return this.rootContext;
    }
    
    POA getNSPOA() {
        return this.nsPOA;
    }
    
    public NamingContext NewContext() throws SystemException {
        try {
            final String newObjectKey = this.contextMgr.getNewObjectKey();
            NamingContextImpl namingContextImpl = new NamingContextImpl(this.theorb, newObjectKey, this, this.contextMgr);
            final NamingContextImpl addContext = this.contextMgr.addContext(newObjectKey, namingContextImpl);
            if (addContext != null) {
                namingContextImpl = addContext;
            }
            namingContextImpl.setServantManagerImpl(this.contextMgr);
            namingContextImpl.setORB(this.theorb);
            namingContextImpl.setRootNameService(this);
            return NamingContextHelper.narrow(this.nsPOA.create_reference_with_id(newObjectKey.getBytes(), NamingContextHelper.id()));
        }
        catch (final SystemException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            return null;
        }
    }
    
    org.omg.CORBA.Object getObjectReferenceFromKey(final String s) {
        org.omg.CORBA.Object create_reference_with_id;
        try {
            create_reference_with_id = this.nsPOA.create_reference_with_id(s.getBytes(), NamingContextHelper.id());
        }
        catch (final Exception ex) {
            create_reference_with_id = null;
        }
        return create_reference_with_id;
    }
    
    String getObjectKey(final org.omg.CORBA.Object object) {
        byte[] reference_to_id;
        try {
            reference_to_id = this.nsPOA.reference_to_id(object);
        }
        catch (final WrongAdapter wrongAdapter) {
            return null;
        }
        catch (final WrongPolicy wrongPolicy) {
            return null;
        }
        catch (final Exception ex) {
            return null;
        }
        return new String(reference_to_id);
    }
}
