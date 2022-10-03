package com.unboundid.util.args;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ArgumentException extends LDAPSDKException
{
    private static final long serialVersionUID = 8353938257797371099L;
    
    public ArgumentException(final String message) {
        super(message);
    }
    
    public ArgumentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
