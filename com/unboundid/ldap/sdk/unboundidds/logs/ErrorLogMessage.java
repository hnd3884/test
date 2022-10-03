package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ErrorLogMessage extends LogMessage
{
    private static final long serialVersionUID = 1743586990943392442L;
    private final ErrorLogCategory category;
    private final ErrorLogSeverity severity;
    private final Long messageID;
    private final Long triggeredByConnectionID;
    private final Long triggeredByOperationID;
    private final String instanceName;
    private final String message;
    private final String productName;
    private final String startupID;
    
    public ErrorLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public ErrorLogMessage(final LogMessage m) {
        super(m);
        this.productName = this.getNamedValue("product");
        this.instanceName = this.getNamedValue("instanceName");
        this.startupID = this.getNamedValue("startupID");
        this.messageID = this.getNamedValueAsLong("msgID");
        this.message = this.getNamedValue("msg");
        this.triggeredByConnectionID = this.getNamedValueAsLong("triggeredByConn");
        this.triggeredByOperationID = this.getNamedValueAsLong("triggeredByOp");
        ErrorLogCategory cat = null;
        try {
            cat = ErrorLogCategory.valueOf(this.getNamedValue("category"));
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
        this.category = cat;
        ErrorLogSeverity sev = null;
        try {
            sev = ErrorLogSeverity.valueOf(this.getNamedValue("severity"));
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
        }
        this.severity = sev;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public String getInstanceName() {
        return this.instanceName;
    }
    
    public String getStartupID() {
        return this.startupID;
    }
    
    public ErrorLogCategory getCategory() {
        return this.category;
    }
    
    public ErrorLogSeverity getSeverity() {
        return this.severity;
    }
    
    public Long getMessageID() {
        return this.messageID;
    }
    
    public Long getTriggeredByConnectionID() {
        return this.triggeredByConnectionID;
    }
    
    public Long getTriggeredByOperationID() {
        return this.triggeredByOperationID;
    }
    
    public String getMessage() {
        return this.message;
    }
}
