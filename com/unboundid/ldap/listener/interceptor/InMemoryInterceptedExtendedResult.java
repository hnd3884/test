package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedExtendedResult extends InMemoryInterceptedResult
{
    ExtendedRequest getRequest();
    
    ExtendedResult getResult();
    
    void setResult(final ExtendedResult p0);
    
    void sendIntermediateResponse(final IntermediateResponse p0) throws LDAPException;
}
