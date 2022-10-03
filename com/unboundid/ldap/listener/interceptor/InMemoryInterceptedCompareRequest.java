package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.sdk.ReadOnlyCompareRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedCompareRequest extends InMemoryInterceptedRequest
{
    ReadOnlyCompareRequest getRequest();
    
    void setRequest(final CompareRequest p0);
}
