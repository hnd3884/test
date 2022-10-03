package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public interface InMemoryInterceptedResult
{
    long getConnectionID();
    
    String getConnectedAddress();
    
    int getConnectedPort();
    
    int getMessageID();
    
    void sendUnsolicitedNotification(final ExtendedResult p0) throws LDAPException;
    
    Object getProperty(final String p0);
}
