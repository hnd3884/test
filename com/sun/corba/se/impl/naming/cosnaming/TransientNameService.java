package com.sun.corba.se.impl.naming.cosnaming;

import org.omg.CORBA.SystemException;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import com.sun.corba.se.impl.logging.NamingSystemException;
import org.omg.CORBA.INITIALIZE;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Object;

public class TransientNameService
{
    private org.omg.CORBA.Object theInitialNamingContext;
    
    public TransientNameService(final ORB orb) throws INITIALIZE {
        this.initialize(orb, "NameService");
    }
    
    public TransientNameService(final ORB orb, final String s) throws INITIALIZE {
        this.initialize(orb, s);
    }
    
    private void initialize(final ORB orb, final String s) throws INITIALIZE {
        final NamingSystemException value = NamingSystemException.get(orb, "naming");
        try {
            final POA poa = (POA)orb.resolve_initial_references("RootPOA");
            poa.the_POAManager().activate();
            int n = 0;
            final Policy[] array = new Policy[3];
            array[n++] = poa.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
            array[n++] = poa.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
            array[n++] = poa.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);
            final POA create_POA = poa.create_POA("TNameService", null, array);
            create_POA.the_POAManager().activate();
            final TransientNamingContext transientNamingContext = new TransientNamingContext(orb, null, create_POA);
            transientNamingContext.localRoot = create_POA.id_to_reference(create_POA.activate_object(transientNamingContext));
            orb.register_initial_reference(s, this.theInitialNamingContext = transientNamingContext.localRoot);
        }
        catch (final SystemException ex) {
            throw value.transNsCannotCreateInitialNcSys(ex);
        }
        catch (final Exception ex2) {
            throw value.transNsCannotCreateInitialNc(ex2);
        }
    }
    
    public org.omg.CORBA.Object initialNamingContext() {
        return this.theInitialNamingContext;
    }
}
