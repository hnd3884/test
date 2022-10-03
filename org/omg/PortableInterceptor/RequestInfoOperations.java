package org.omg.PortableInterceptor;

import org.omg.IOP.ServiceContext;
import org.omg.CORBA.Object;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;

public interface RequestInfoOperations
{
    int request_id();
    
    String operation();
    
    Parameter[] arguments();
    
    TypeCode[] exceptions();
    
    String[] contexts();
    
    String[] operation_context();
    
    Any result();
    
    boolean response_expected();
    
    short sync_scope();
    
    short reply_status();
    
    org.omg.CORBA.Object forward_reference();
    
    Any get_slot(final int p0) throws InvalidSlot;
    
    ServiceContext get_request_service_context(final int p0);
    
    ServiceContext get_reply_service_context(final int p0);
}
