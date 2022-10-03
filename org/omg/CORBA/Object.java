package org.omg.CORBA;

public interface Object
{
    boolean _is_a(final String p0);
    
    boolean _is_equivalent(final Object p0);
    
    boolean _non_existent();
    
    int _hash(final int p0);
    
    Object _duplicate();
    
    void _release();
    
    Object _get_interface_def();
    
    Request _request(final String p0);
    
    Request _create_request(final Context p0, final String p1, final NVList p2, final NamedValue p3);
    
    Request _create_request(final Context p0, final String p1, final NVList p2, final NamedValue p3, final ExceptionList p4, final ContextList p5);
    
    Policy _get_policy(final int p0);
    
    DomainManager[] _get_domain_managers();
    
    Object _set_policy_override(final Policy[] p0, final SetOverrideType p1);
}
