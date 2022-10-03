package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.ExtendedRequestProtocolOp;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedExtendedOperation extends InterceptedOperation implements InMemoryInterceptedExtendedRequest, InMemoryInterceptedExtendedResult
{
    private ExtendedRequest extendedRequest;
    private ExtendedResult extendedResult;
    
    InterceptedExtendedOperation(final LDAPListenerClientConnection clientConnection, final int messageID, final ExtendedRequestProtocolOp requestOp, final Control... requestControls) {
        super(clientConnection, messageID);
        this.extendedRequest = requestOp.toExtendedRequest(requestControls);
        this.extendedResult = null;
    }
    
    @Override
    public ExtendedRequest getRequest() {
        return this.extendedRequest;
    }
    
    @Override
    public void setRequest(final ExtendedRequest extendedRequest) {
        this.extendedRequest = extendedRequest;
    }
    
    @Override
    public ExtendedResult getResult() {
        return this.extendedResult;
    }
    
    @Override
    public void setResult(final ExtendedResult extendedResult) {
        this.extendedResult = extendedResult;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedExtendedOperation(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.extendedRequest);
        buffer.append(", result=");
        buffer.append(this.extendedResult);
        buffer.append(')');
    }
}
