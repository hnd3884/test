package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedExtendedRequest extends InMemoryInterceptedRequest
{
    ExtendedRequest getRequest();
    
    void setRequest(final ExtendedRequest p0);
}
