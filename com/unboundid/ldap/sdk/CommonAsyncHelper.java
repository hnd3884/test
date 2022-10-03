package com.unboundid.ldap.sdk;

interface CommonAsyncHelper extends ResponseAcceptor
{
    AsyncRequestID getAsyncRequestID();
    
    LDAPConnection getConnection();
    
    long getCreateTimeNanos();
    
    OperationType getOperationType();
}
