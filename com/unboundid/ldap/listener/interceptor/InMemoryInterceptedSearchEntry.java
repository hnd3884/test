package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.ReadOnlySearchRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedSearchEntry extends InMemoryInterceptedResult
{
    ReadOnlySearchRequest getRequest();
    
    SearchResultEntry getSearchEntry();
    
    void setSearchEntry(final Entry p0);
}
