package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ReadOnlyDeleteRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.DeleteRequestProtocolOp;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedDeleteOperation extends InterceptedOperation implements InMemoryInterceptedDeleteRequest, InMemoryInterceptedDeleteResult
{
    private DeleteRequest deleteRequest;
    private LDAPResult deleteResult;
    
    InterceptedDeleteOperation(final LDAPListenerClientConnection clientConnection, final int messageID, final DeleteRequestProtocolOp requestOp, final Control... requestControls) {
        super(clientConnection, messageID);
        this.deleteRequest = requestOp.toDeleteRequest(requestControls);
        this.deleteResult = null;
    }
    
    @Override
    public ReadOnlyDeleteRequest getRequest() {
        return this.deleteRequest;
    }
    
    @Override
    public void setRequest(final DeleteRequest deleteRequest) {
        this.deleteRequest = deleteRequest;
    }
    
    @Override
    public LDAPResult getResult() {
        return this.deleteResult;
    }
    
    @Override
    public void setResult(final LDAPResult deleteResult) {
        this.deleteResult = deleteResult;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedDeleteOperation(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.deleteRequest);
        buffer.append(", result=");
        buffer.append(this.deleteResult);
        buffer.append(')');
    }
}
