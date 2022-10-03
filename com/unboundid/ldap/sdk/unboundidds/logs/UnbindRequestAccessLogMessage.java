package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UnbindRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = 837856533259958468L;
    
    public UnbindRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public UnbindRequestAccessLogMessage(final LogMessage m) {
        super(m);
    }
    
    @Override
    public AccessLogOperationType getOperationType() {
        return AccessLogOperationType.UNBIND;
    }
}
