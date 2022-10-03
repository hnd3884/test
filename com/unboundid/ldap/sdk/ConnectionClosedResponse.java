package com.unboundid.ldap.sdk;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;
import java.io.Serializable;
import com.unboundid.ldap.protocol.LDAPResponse;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class ConnectionClosedResponse implements LDAPResponse, Serializable
{
    private static final long serialVersionUID = -3931112652935496193L;
    private final ResultCode resultCode;
    private final String message;
    
    ConnectionClosedResponse(final ResultCode resultCode, final String message) {
        this.resultCode = resultCode;
        this.message = message;
    }
    
    @Override
    public int getMessageID() {
        return -1;
    }
    
    String getMessage() {
        return this.message;
    }
    
    ResultCode getResultCode() {
        return this.resultCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ConnectionClosedResponse(resultCode='");
        buffer.append(this.resultCode);
        buffer.append('\'');
        if (this.message != null) {
            buffer.append(", message='");
            buffer.append(this.message);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
