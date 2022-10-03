package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.ReadOnlyModifyDNRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedModifyDNRequest extends InMemoryInterceptedRequest
{
    ReadOnlyModifyDNRequest getRequest();
    
    void setRequest(final ModifyDNRequest p0);
}
