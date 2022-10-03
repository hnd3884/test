package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AddAssuranceCompletedAccessLogMessage extends AddResultAccessLogMessage
{
    private static final long serialVersionUID = -1941067013045984669L;
    private final Boolean localAssuranceSatisfied;
    private final Boolean remoteAssuranceSatisfied;
    private final String serverAssuranceResults;
    
    public AddAssuranceCompletedAccessLogMessage(final String s) throws LogException {
        this(new LogMessage(s));
    }
    
    public AddAssuranceCompletedAccessLogMessage(final LogMessage m) {
        super(m);
        this.localAssuranceSatisfied = this.getNamedValueAsBoolean("localAssuranceSatisfied");
        this.remoteAssuranceSatisfied = this.getNamedValueAsBoolean("remoteAssuranceSatisfied");
        this.serverAssuranceResults = this.getNamedValue("serverAssuranceResults");
    }
    
    public Boolean getLocalAssuranceSatisfied() {
        return this.localAssuranceSatisfied;
    }
    
    public Boolean getRemoteAssuranceSatisfied() {
        return this.remoteAssuranceSatisfied;
    }
    
    public String getServerAssuranceResults() {
        return this.serverAssuranceResults;
    }
    
    @Override
    public AccessLogMessageType getMessageType() {
        return AccessLogMessageType.ASSURANCE_COMPLETE;
    }
}
