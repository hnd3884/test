package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Extensible;

@Extensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPExtendedResponse extends LDAPResponse
{
    private static final long serialVersionUID = 7956345950545720834L;
    private final ExtendedResult extendedResult;
    
    public LDAPExtendedResponse(final ExtendedResult extendedResult) {
        super(extendedResult);
        this.extendedResult = extendedResult;
    }
    
    public String getID() {
        return this.extendedResult.getOID();
    }
    
    public byte[] getValue() {
        final ASN1OctetString value = this.extendedResult.getValue();
        if (value == null) {
            return null;
        }
        return value.getValue();
    }
    
    public final ExtendedResult toExtendedResult() {
        return this.extendedResult;
    }
    
    @Override
    public String toString() {
        return this.extendedResult.toString();
    }
}
