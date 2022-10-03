package com.unboundid.ldap.sdk.unboundidds.logs;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AuditLogException extends LDAPSDKException
{
    private static final long serialVersionUID = -3928437646247214211L;
    private final List<String> logMessageLines;
    
    public AuditLogException(final List<String> logMessageLines, final String explanation) {
        this(logMessageLines, explanation, null);
    }
    
    public AuditLogException(final List<String> logMessageLines, final String explanation, final Throwable cause) {
        super(explanation, cause);
        Validator.ensureNotNull(logMessageLines);
        Validator.ensureNotNull(explanation);
        this.logMessageLines = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(logMessageLines));
    }
    
    public List<String> getLogMessageLines() {
        return this.logMessageLines;
    }
}
