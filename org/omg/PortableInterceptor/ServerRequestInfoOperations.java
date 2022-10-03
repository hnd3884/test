package org.omg.PortableInterceptor;

import org.omg.IOP.ServiceContext;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Any;

public interface ServerRequestInfoOperations extends RequestInfoOperations
{
    Any sending_exception();
    
    byte[] object_id();
    
    byte[] adapter_id();
    
    String server_id();
    
    String orb_id();
    
    String[] adapter_name();
    
    String target_most_derived_interface();
    
    Policy get_server_policy(final int p0);
    
    void set_slot(final int p0, final Any p1) throws InvalidSlot;
    
    boolean target_is_a(final String p0);
    
    void add_reply_service_context(final ServiceContext p0, final boolean p1);
}
