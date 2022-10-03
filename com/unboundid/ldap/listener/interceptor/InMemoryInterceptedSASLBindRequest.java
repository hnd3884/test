package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.GenericSASLBindRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedSASLBindRequest extends InMemoryInterceptedRequest
{
    GenericSASLBindRequest getRequest();
    
    void setRequest(final GenericSASLBindRequest p0);
}
