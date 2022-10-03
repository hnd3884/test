package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class AbandonRequestAccessLogMessage extends OperationRequestAccessLogMessage
{
    private static final long serialVersionUID = 4681707907192987394L;
    private final Integer idToAbandon;
    
    public AbandonRequestAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public AbandonRequestAccessLogMessage(final LogMessage m) {
        super(m);
        this.idToAbandon = this.getNamedValueAsInteger("idToAbandon");
    }
    
    public final Integer getMessageIDToAbandon() {
        return this.idToAbandon;
    }
    
    @Override
    public final AccessLogOperationType getOperationType() {
        return AccessLogOperationType.ABANDON;
    }
}
