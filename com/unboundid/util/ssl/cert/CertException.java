package com.unboundid.util.ssl.cert;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CertException extends LDAPSDKException
{
    private static final long serialVersionUID = -4999182955315408793L;
    
    public CertException(final String message) {
        super(message);
    }
    
    public CertException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
