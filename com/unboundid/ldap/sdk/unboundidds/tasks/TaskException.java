package com.unboundid.ldap.sdk.unboundidds.tasks;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TaskException extends LDAPSDKException
{
    private static final long serialVersionUID = -6332009666776649856L;
    
    public TaskException(final String message) {
        super(message);
    }
    
    public TaskException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
