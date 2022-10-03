package org.omg.PortableInterceptor;

import org.omg.IOP.TaggedComponent;
import org.omg.CORBA.Policy;

public interface IORInfoOperations
{
    Policy get_effective_policy(final int p0);
    
    void add_ior_component(final TaggedComponent p0);
    
    void add_ior_component_to_profile(final TaggedComponent p0, final int p1);
    
    int manager_id();
    
    short state();
    
    ObjectReferenceTemplate adapter_template();
    
    ObjectReferenceFactory current_factory();
    
    void current_factory(final ObjectReferenceFactory p0);
}
