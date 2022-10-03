package org.owasp.esapi.reference.accesscontrol.policyloader;

public interface PolicyParameters
{
    Object get(final String p0);
    
    void set(final String p0, final Object p1) throws IllegalArgumentException;
    
    void put(final String p0, final Object p1) throws IllegalArgumentException;
    
    void lock();
}
