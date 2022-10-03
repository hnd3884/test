package org.omg.PortableInterceptor;

import org.omg.IOP.ServiceContext;
import org.omg.CORBA.Policy;
import org.omg.IOP.TaggedComponent;
import org.omg.CORBA.Any;
import org.omg.IOP.TaggedProfile;
import org.omg.CORBA.Object;

public interface ClientRequestInfoOperations extends RequestInfoOperations
{
    org.omg.CORBA.Object target();
    
    org.omg.CORBA.Object effective_target();
    
    TaggedProfile effective_profile();
    
    Any received_exception();
    
    String received_exception_id();
    
    TaggedComponent get_effective_component(final int p0);
    
    TaggedComponent[] get_effective_components(final int p0);
    
    Policy get_request_policy(final int p0);
    
    void add_request_service_context(final ServiceContext p0, final boolean p1);
}
