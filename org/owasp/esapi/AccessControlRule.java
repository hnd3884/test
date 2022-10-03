package org.owasp.esapi;

public interface AccessControlRule<P, R>
{
    void setPolicyParameters(final P p0);
    
    P getPolicyParameters();
    
    boolean isAuthorized(final R p0) throws Exception;
}
