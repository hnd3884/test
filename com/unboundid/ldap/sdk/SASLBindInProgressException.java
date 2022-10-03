package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SASLBindInProgressException extends LDAPBindException
{
    private static final long serialVersionUID = -2483660992461709721L;
    
    SASLBindInProgressException(final BindResult bindResult) {
        super(bindResult);
    }
    
    @Override
    public BindResult getBindResult() {
        return super.getBindResult();
    }
    
    @Override
    public ASN1OctetString getServerSASLCredentials() {
        return super.getServerSASLCredentials();
    }
}
