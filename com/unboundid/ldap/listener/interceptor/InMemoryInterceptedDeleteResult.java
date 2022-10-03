package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ReadOnlyDeleteRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedDeleteResult extends InMemoryInterceptedResult
{
    ReadOnlyDeleteRequest getRequest();
    
    LDAPResult getResult();
    
    void setResult(final LDAPResult p0);
    
    void sendIntermediateResponse(final IntermediateResponse p0) throws LDAPException;
}
