package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class ExtendedRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = -4278715896574532061L;
    private final String requestOID;
    
    public ExtendedRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public ExtendedRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.requestOID = this.getNamedValue("requestOID");
    }
    
    public final String getRequestOID() {
        return this.requestOID;
    }
    
    @Override
    public final AccessLogOperationType getOperationType() {
        return AccessLogOperationType.EXTENDED;
    }
}
