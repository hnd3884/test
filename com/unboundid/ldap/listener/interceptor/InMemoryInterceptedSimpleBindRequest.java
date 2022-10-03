package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedSimpleBindRequest extends InMemoryInterceptedRequest
{
    SimpleBindRequest getRequest();
    
    void setRequest(final SimpleBindRequest p0);
}
