package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ReadOnlyModifyDNRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.ModifyDNRequestProtocolOp;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedModifyDNOperation extends InterceptedOperation implements InMemoryInterceptedModifyDNRequest, InMemoryInterceptedModifyDNResult
{
    private ModifyDNRequest modifyDNRequest;
    private LDAPResult modifyDNResult;
    
    InterceptedModifyDNOperation(final LDAPListenerClientConnection clientConnection, final int messageID, final ModifyDNRequestProtocolOp requestOp, final Control... requestControls) {
        super(clientConnection, messageID);
        this.modifyDNRequest = requestOp.toModifyDNRequest(requestControls);
        this.modifyDNResult = null;
    }
    
    @Override
    public ReadOnlyModifyDNRequest getRequest() {
        return this.modifyDNRequest;
    }
    
    @Override
    public void setRequest(final ModifyDNRequest modifyDNRequest) {
        this.modifyDNRequest = modifyDNRequest;
    }
    
    @Override
    public LDAPResult getResult() {
        return this.modifyDNResult;
    }
    
    @Override
    public void setResult(final LDAPResult modifyDNResult) {
        this.modifyDNResult = modifyDNResult;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedModifyDNOperation(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.modifyDNRequest);
        buffer.append(", result=");
        buffer.append(this.modifyDNResult);
        buffer.append(')');
    }
}
