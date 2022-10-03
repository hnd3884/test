package com.unboundid.ldap.sdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPBindException extends LDAPException
{
    private static final long serialVersionUID = 6545956074186731236L;
    private final BindResult bindResult;
    
    public LDAPBindException(final BindResult bindResult) {
        super(bindResult);
        this.bindResult = bindResult;
    }
    
    @Override
    public LDAPResult toLDAPResult() {
        return this.bindResult;
    }
    
    public BindResult getBindResult() {
        return this.bindResult;
    }
    
    public ASN1OctetString getServerSASLCredentials() {
        return this.bindResult.getServerSASLCredentials();
    }
}
