package com.unboundid.ldap.sdk.unboundidds.logs;

import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LogException extends LDAPSDKException
{
    private static final long serialVersionUID = -5936254058683765082L;
    private final String logMessage;
    
    public LogException(final String logMessage, final String explanation) {
        this(logMessage, explanation, null);
    }
    
    public LogException(final String logMessage, final String explanation, final Throwable cause) {
        super(explanation, cause);
        Validator.ensureNotNull(logMessage, explanation);
        this.logMessage = logMessage;
    }
    
    public String getLogMessage() {
        return this.logMessage;
    }
}
