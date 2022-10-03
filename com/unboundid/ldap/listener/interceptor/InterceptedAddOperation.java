package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ReadOnlyAddRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.AddRequestProtocolOp;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedAddOperation extends InterceptedOperation implements InMemoryInterceptedAddRequest, InMemoryInterceptedAddResult
{
    private AddRequest addRequest;
    private LDAPResult addResult;
    
    InterceptedAddOperation(final LDAPListenerClientConnection clientConnection, final int messageID, final AddRequestProtocolOp requestOp, final Control... requestControls) {
        super(clientConnection, messageID);
        this.addRequest = requestOp.toAddRequest(requestControls);
        this.addResult = null;
    }
    
    @Override
    public ReadOnlyAddRequest getRequest() {
        return this.addRequest;
    }
    
    @Override
    public void setRequest(final AddRequest addRequest) {
        this.addRequest = addRequest;
    }
    
    @Override
    public LDAPResult getResult() {
        return this.addResult;
    }
    
    @Override
    public void setResult(final LDAPResult addResult) {
        this.addResult = addResult;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedAddOperation(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.addRequest);
        buffer.append(", result=");
        buffer.append(this.addResult);
        buffer.append(')');
    }
}
