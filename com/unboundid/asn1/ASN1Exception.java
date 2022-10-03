package com.unboundid.asn1;

import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.LDAPSDKException;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ASN1Exception extends LDAPSDKException
{
    private static final long serialVersionUID = 3234714599495723483L;
    
    public ASN1Exception(final String message) {
        super(message);
    }
    
    public ASN1Exception(final String message, final Throwable cause) {
        super(message, cause);
    }
}
