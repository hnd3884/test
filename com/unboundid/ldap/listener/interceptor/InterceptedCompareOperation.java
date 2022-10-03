package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ReadOnlyCompareRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.CompareRequestProtocolOp;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedCompareOperation extends InterceptedOperation implements InMemoryInterceptedCompareRequest, InMemoryInterceptedCompareResult
{
    private CompareRequest compareRequest;
    private LDAPResult compareResult;
    
    InterceptedCompareOperation(final LDAPListenerClientConnection clientConnection, final int messageID, final CompareRequestProtocolOp requestOp, final Control... requestControls) {
        super(clientConnection, messageID);
        this.compareRequest = requestOp.toCompareRequest(requestControls);
        this.compareResult = null;
    }
    
    @Override
    public ReadOnlyCompareRequest getRequest() {
        return this.compareRequest;
    }
    
    @Override
    public void setRequest(final CompareRequest compareRequest) {
        this.compareRequest = compareRequest;
    }
    
    @Override
    public LDAPResult getResult() {
        return this.compareResult;
    }
    
    @Override
    public void setResult(final LDAPResult compareResult) {
        this.compareResult = compareResult;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedCompareOperation(");
        this.appendCommonToString(buffer);
        buffer.append(", request=");
        buffer.append(this.compareRequest);
        buffer.append(", result=");
        buffer.append(this.compareResult);
        buffer.append(')');
    }
}
