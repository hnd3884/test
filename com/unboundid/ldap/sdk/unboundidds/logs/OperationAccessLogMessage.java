package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public abstract class OperationAccessLogMessage extends AccessLogMessage
{
    private static final long serialVersionUID = 5311424730889643655L;
    private final Integer messageID;
    private final Long operationID;
    private final Long triggeredByConnectionID;
    private final Long triggeredByOperationID;
    private final String origin;
    
    protected OperationAccessLogMessage(final LogMessage m) {
        super(m);
        this.messageID = this.getNamedValueAsInteger("msgID");
        this.operationID = this.getNamedValueAsLong("op");
        this.triggeredByConnectionID = this.getNamedValueAsLong("triggeredByConn");
        this.triggeredByOperationID = this.getNamedValueAsLong("triggeredByOp");
        this.origin = this.getNamedValue("origin");
    }
    
    public final Long getOperationID() {
        return this.operationID;
    }
    
    public final Long getTriggeredByConnectionID() {
        return this.triggeredByConnectionID;
    }
    
    public final Long getTriggeredByOperationID() {
        return this.triggeredByOperationID;
    }
    
    public final Integer getMessageID() {
        return this.messageID;
    }
    
    public final String getOrigin() {
        return this.origin;
    }
    
    public abstract AccessLogOperationType getOperationType();
}
