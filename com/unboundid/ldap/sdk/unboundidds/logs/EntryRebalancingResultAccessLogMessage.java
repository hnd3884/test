package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class EntryRebalancingResultAccessLogMessage extends EntryRebalancingRequestAccessLogMessage
{
    private static final long serialVersionUID = -5593721315305821425L;
    private final Boolean sourceAltered;
    private final Boolean targetAltered;
    private final Integer entriesAddedToTarget;
    private final Integer entriesDeletedFromSource;
    private final Integer entriesReadFromSource;
    private final ResultCode resultCode;
    private final String adminActionRequired;
    private final String errorMessage;
    
    public EntryRebalancingResultAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public EntryRebalancingResultAccessLogMessage(final LogMessage m) {
        super(m);
        final Integer rcInteger = this.getNamedValueAsInteger("resultCode");
        if (rcInteger == null) {
            this.resultCode = null;
        }
        else {
            this.resultCode = ResultCode.valueOf(rcInteger);
        }
        this.adminActionRequired = this.getNamedValue("adminActionRequired");
        this.entriesAddedToTarget = this.getNamedValueAsInteger("entriesAddedToTarget");
        this.entriesDeletedFromSource = this.getNamedValueAsInteger("entriesDeletedFromSource");
        this.entriesReadFromSource = this.getNamedValueAsInteger("entriesReadFromSource");
        this.errorMessage = this.getNamedValue("errorMessage");
        this.sourceAltered = this.getNamedValueAsBoolean("sourceAltered");
        this.targetAltered = this.getNamedValueAsBoolean("targetAltered");
    }
    
    public ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    public String getAdminActionRequired() {
        return this.adminActionRequired;
    }
    
    public Boolean sourceAltered() {
        return this.sourceAltered;
    }
    
    public Boolean targetAltered() {
        return this.targetAltered;
    }
    
    public Integer getEntriesReadFromSource() {
        return this.entriesReadFromSource;
    }
    
    public Integer getEntriesAddedToTarget() {
        return this.entriesAddedToTarget;
    }
    
    public Integer getEntriesDeletedFromSource() {
        return this.entriesDeletedFromSource;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.ENTRY_REBALANCING_RESULT;
    }
}
