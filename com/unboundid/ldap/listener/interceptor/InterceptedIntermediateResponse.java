package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.protocol.IntermediateResponseProtocolOp;
import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
final class InterceptedIntermediateResponse extends InterceptedOperation implements InMemoryInterceptedIntermediateResponse
{
    private final InterceptedOperation op;
    private IntermediateResponse response;
    
    InterceptedIntermediateResponse(final InterceptedOperation op, final IntermediateResponseProtocolOp response, final Control... responseControls) {
        super(op);
        this.op = op;
        this.response = response.toIntermediateResponse(responseControls);
    }
    
    @Override
    public InMemoryInterceptedRequest getRequest() {
        return this.op;
    }
    
    @Override
    public IntermediateResponse getIntermediateResponse() {
        return this.response;
    }
    
    @Override
    public void setIntermediateResponse(final IntermediateResponse response) {
        this.response = response;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("InterceptedIntermediateResponse(");
        buffer.append("op=");
        buffer.append(this.op);
        buffer.append(", response=");
        buffer.append(this.response);
        buffer.append(')');
    }
}
