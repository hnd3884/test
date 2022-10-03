package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedSearchResult extends InMemoryInterceptedResult
{
    ReadOnlySearchRequest getRequest();
    
    LDAPResult getResult();
    
    void setResult(final LDAPResult p0);
    
    void sendSearchEntry(final Entry p0) throws LDAPException;
    
    void sendSearchReference(final SearchResultReference p0) throws LDAPException;
    
    void sendIntermediateResponse(final IntermediateResponse p0) throws LDAPException;
}
