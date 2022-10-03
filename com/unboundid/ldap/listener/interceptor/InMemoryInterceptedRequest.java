package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedRequest
{
    long getConnectionID();
    
    String getConnectedAddress();
    
    int getConnectedPort();
    
    int getMessageID();
    
    void sendIntermediateResponse(final IntermediateResponse p0) throws LDAPException;
    
    void sendUnsolicitedNotification(final ExtendedResult p0) throws LDAPException;
    
    Object getProperty(final String p0);
    
    Object setProperty(final String p0, final Object p1);
}
