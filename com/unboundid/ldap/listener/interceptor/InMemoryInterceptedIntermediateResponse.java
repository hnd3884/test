package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedIntermediateResponse extends InMemoryInterceptedResult
{
    InMemoryInterceptedRequest getRequest();
    
    IntermediateResponse getIntermediateResponse();
    
    void setIntermediateResponse(final IntermediateResponse p0);
}
