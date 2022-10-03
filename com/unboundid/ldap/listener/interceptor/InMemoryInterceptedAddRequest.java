package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.ReadOnlyAddRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedAddRequest extends InMemoryInterceptedRequest
{
    ReadOnlyAddRequest getRequest();
    
    void setRequest(final AddRequest p0);
}
