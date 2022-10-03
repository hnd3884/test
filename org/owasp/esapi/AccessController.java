package org.owasp.esapi;

import org.owasp.esapi.errors.AccessControlException;

public interface AccessController
{
    boolean isAuthorized(final Object p0, final Object p1);
    
    void assertAuthorized(final Object p0, final Object p1) throws AccessControlException;
    
    boolean isAuthorizedForURL(final String p0);
    
    boolean isAuthorizedForFunction(final String p0);
    
    boolean isAuthorizedForData(final String p0, final Object p1);
    
    boolean isAuthorizedForFile(final String p0);
    
    boolean isAuthorizedForService(final String p0);
    
    void assertAuthorizedForURL(final String p0) throws AccessControlException;
    
    void assertAuthorizedForFunction(final String p0) throws AccessControlException;
    
    void assertAuthorizedForData(final String p0, final Object p1) throws AccessControlException;
    
    void assertAuthorizedForFile(final String p0) throws AccessControlException;
    
    void assertAuthorizedForService(final String p0) throws AccessControlException;
}
