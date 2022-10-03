package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ReadOnlyModifyRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.ModifyRequestProtocolOp;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedModifyOperation extends InterceptedOperation implements InMemoryInterceptedModifyRequest, InMemoryInterceptedModifyResult
{
    private ModifyRequest modifyRequest;
    private LDAPResult modifyResult;
    
    InterceptedModifyOperation(final LDAPListenerClientConnection clientConnection, final int messageID, final ModifyRequestProtocolOp requestOp, final Control... requestControls) {
        super(clientConnection, messageID);
        this.modifyRequest = requestOp.toModifyRequest(requestControls);
        this.modifyResult = null;
    }
    
    @Override
    public ReadOnlyModifyRequest getRequest() {
        return this.modifyRequest;
    }
    
    @Override
    public void setRequest(final ModifyRequest modifyRequest) {
        this.modifyRequest = modifyRequest;
    }
    
    @Override
    public LDAPResult getResult() {
        return this.modifyResult;
    }
    
    @Override
    public void setResult(final LDAPResult modifyResult) {
        this.modifyResult = modifyResult;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedModifyOperation(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.modifyRequest);
        buffer.append(", result=");
        buffer.append(this.modifyResult);
        buffer.append(')');
    }
}
