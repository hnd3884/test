package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.GenericSASLBindRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedSASLBindResult extends InMemoryInterceptedResult
{
    GenericSASLBindRequest getRequest();
    
    BindResult getResult();
    
    void setResult(final BindResult p0);
    
    void sendIntermediateResponse(final IntermediateResponse p0) throws LDAPException;
}
