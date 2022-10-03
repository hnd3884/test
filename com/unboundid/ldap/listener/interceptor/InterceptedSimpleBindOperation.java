package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.BindRequestProtocolOp;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedSimpleBindOperation extends InterceptedOperation implements InMemoryInterceptedSimpleBindRequest, InMemoryInterceptedSimpleBindResult
{
    private BindResult bindResult;
    private SimpleBindRequest bindRequest;
    
    InterceptedSimpleBindOperation(final LDAPListenerClientConnection clientConnection, final int messageID, final BindRequestProtocolOp requestOp, final Control... requestControls) {
        super(clientConnection, messageID);
        this.bindRequest = (SimpleBindRequest)requestOp.toBindRequest(requestControls);
        this.bindResult = null;
    }
    
    @Override
    public SimpleBindRequest getRequest() {
        return this.bindRequest;
    }
    
    @Override
    public void setRequest(final SimpleBindRequest bindRequest) {
        this.bindRequest = bindRequest;
    }
    
    @Override
    public BindResult getResult() {
        return this.bindResult;
    }
    
    @Override
    public void setResult(final BindResult bindResult) {
        this.bindResult = bindResult;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedSimpleBindOperation(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.bindRequest);
        buffer.append(", result=");
        buffer.append(this.bindResult);
        buffer.append(')');
    }
}
