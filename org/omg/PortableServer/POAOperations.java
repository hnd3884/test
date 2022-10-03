package org.omg.PortableServer;

import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.CORBA.Policy;

public interface POAOperations
{
    POA create_POA(final String p0, final POAManager p1, final Policy[] p2) throws AdapterAlreadyExists, InvalidPolicy;
    
    POA find_POA(final String p0, final boolean p1) throws AdapterNonExistent;
    
    void destroy(final boolean p0, final boolean p1);
    
    ThreadPolicy create_thread_policy(final ThreadPolicyValue p0);
    
    LifespanPolicy create_lifespan_policy(final LifespanPolicyValue p0);
    
    IdUniquenessPolicy create_id_uniqueness_policy(final IdUniquenessPolicyValue p0);
    
    IdAssignmentPolicy create_id_assignment_policy(final IdAssignmentPolicyValue p0);
    
    ImplicitActivationPolicy create_implicit_activation_policy(final ImplicitActivationPolicyValue p0);
    
    ServantRetentionPolicy create_servant_retention_policy(final ServantRetentionPolicyValue p0);
    
    RequestProcessingPolicy create_request_processing_policy(final RequestProcessingPolicyValue p0);
    
    String the_name();
    
    POA the_parent();
    
    POA[] the_children();
    
    POAManager the_POAManager();
    
    AdapterActivator the_activator();
    
    void the_activator(final AdapterActivator p0);
    
    ServantManager get_servant_manager() throws WrongPolicy;
    
    void set_servant_manager(final ServantManager p0) throws WrongPolicy;
    
    Servant get_servant() throws NoServant, WrongPolicy;
    
    void set_servant(final Servant p0) throws WrongPolicy;
    
    byte[] activate_object(final Servant p0) throws ServantAlreadyActive, WrongPolicy;
    
    void activate_object_with_id(final byte[] p0, final Servant p1) throws ServantAlreadyActive, ObjectAlreadyActive, WrongPolicy;
    
    void deactivate_object(final byte[] p0) throws ObjectNotActive, WrongPolicy;
    
    org.omg.CORBA.Object create_reference(final String p0) throws WrongPolicy;
    
    org.omg.CORBA.Object create_reference_with_id(final byte[] p0, final String p1);
    
    byte[] servant_to_id(final Servant p0) throws ServantNotActive, WrongPolicy;
    
    org.omg.CORBA.Object servant_to_reference(final Servant p0) throws ServantNotActive, WrongPolicy;
    
    Servant reference_to_servant(final org.omg.CORBA.Object p0) throws ObjectNotActive, WrongPolicy, WrongAdapter;
    
    byte[] reference_to_id(final org.omg.CORBA.Object p0) throws WrongAdapter, WrongPolicy;
    
    Servant id_to_servant(final byte[] p0) throws ObjectNotActive, WrongPolicy;
    
    org.omg.CORBA.Object id_to_reference(final byte[] p0) throws ObjectNotActive, WrongPolicy;
    
    byte[] id();
}
