package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedSearchReference extends InMemoryInterceptedResult
{
    ReadOnlySearchRequest getRequest();
    
    SearchResultReference getSearchReference();
    
    void setSearchReference(final SearchResultReference p0);
}
