package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ReadOnlyModifyRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedModifyRequest extends InMemoryInterceptedRequest
{
    ReadOnlyModifyRequest getRequest();
    
    void setRequest(final ModifyRequest p0);
}
