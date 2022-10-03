package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.ReadOnlyDeleteRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedDeleteRequest extends InMemoryInterceptedRequest
{
    ReadOnlyDeleteRequest getRequest();
    
    void setRequest(final DeleteRequest p0);
}
