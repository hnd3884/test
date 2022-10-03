package com.unboundid.ldap.sdk.migrate.ldapjdk;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class LDAPExtendedOperation implements Serializable
{
    private static final long serialVersionUID = 9207085503424216431L;
    private final byte[] value;
    private final String oid;
    
    public LDAPExtendedOperation(final String id, final byte[] vals) {
        this.oid = id;
        this.value = vals;
    }
    
    public LDAPExtendedOperation(final ExtendedRequest extendedRequest) {
        this.oid = extendedRequest.getOID();
        final ASN1OctetString v = extendedRequest.getValue();
        if (v == null) {
            this.value = null;
        }
        else {
            this.value = v.getValue();
        }
    }
    
    public String getID() {
        return this.oid;
    }
    
    public byte[] getValue() {
        return this.value;
    }
    
    public final ExtendedRequest toExtendedRequest() {
        if (this.value == null) {
            return new ExtendedRequest(this.oid);
        }
        return new ExtendedRequest(this.oid, new ASN1OctetString(this.value));
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("LDAPExtendedOperation(id=");
        buffer.append(this.oid);
        if (this.value != null) {
            buffer.append(", value=byte[");
            buffer.append(this.value.length);
            buffer.append(']');
        }
        buffer.append(')');
        return buffer.toString();
    }
}
