package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedSearchRequest extends InMemoryInterceptedRequest
{
    ReadOnlySearchRequest getRequest();
    
    void setRequest(final SearchRequest p0);
    
    void sendSearchEntry(final Entry p0) throws LDAPException;
    
    void sendSearchReference(final SearchResultReference p0) throws LDAPException;
}
