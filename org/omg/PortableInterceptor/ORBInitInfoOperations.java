package org.omg.PortableInterceptor;

import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.IOP.CodecFactory;

public interface ORBInitInfoOperations
{
    String[] arguments();
    
    String orb_id();
    
    CodecFactory codec_factory();
    
    void register_initial_reference(final String p0, final org.omg.CORBA.Object p1) throws InvalidName;
    
    org.omg.CORBA.Object resolve_initial_references(final String p0) throws InvalidName;
    
    void add_client_request_interceptor(final ClientRequestInterceptor p0) throws DuplicateName;
    
    void add_server_request_interceptor(final ServerRequestInterceptor p0) throws DuplicateName;
    
    void add_ior_interceptor(final IORInterceptor p0) throws DuplicateName;
    
    int allocate_slot_id();
    
    void register_policy_factory(final int p0, final PolicyFactory p1);
}
