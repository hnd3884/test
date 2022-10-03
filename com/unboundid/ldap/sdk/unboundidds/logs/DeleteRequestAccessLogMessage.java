package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class DeleteRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = 4562376555035497481L;
    private final String dn;
    
    public DeleteRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public DeleteRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.dn = this.getNamedValue("dn");
    }
    
    public final String getDN() {
        return this.dn;
    }
    
    @Override
    public final AccessLogOperationType getOperationType() {
        return AccessLogOperationType.DELETE;
    }
}
