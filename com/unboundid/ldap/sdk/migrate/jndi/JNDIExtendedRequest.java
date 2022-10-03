package com.unboundid.ldap.sdk.migrate.jndi;

import javax.naming.ldap.ExtendedResponse;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import javax.naming.NamingException;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.naming.ldap.ExtendedRequest;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JNDIExtendedRequest implements ExtendedRequest
{
    private static final long serialVersionUID = -8502230539753937274L;
    private final com.unboundid.ldap.sdk.ExtendedRequest r;
    
    public JNDIExtendedRequest(final com.unboundid.ldap.sdk.ExtendedRequest r) {
        this.r = r;
    }
    
    public JNDIExtendedRequest(final ExtendedRequest r) throws NamingException {
        this.r = toSDKExtendedRequest(r);
    }
    
    @Override
    public String getID() {
        return this.r.getOID();
    }
    
    @Override
    public byte[] getEncodedValue() {
        final ASN1OctetString value = this.r.getValue();
        if (value == null) {
            return null;
        }
        return value.encode();
    }
    
    @Override
    public JNDIExtendedResponse createExtendedResponse(final String id, final byte[] berValue, final int offset, final int length) throws NamingException {
        return new JNDIExtendedResponse(id, berValue, offset, length);
    }
    
    public com.unboundid.ldap.sdk.ExtendedRequest toSDKExtendedRequest() {
        return this.r;
    }
    
    public static com.unboundid.ldap.sdk.ExtendedRequest toSDKExtendedRequest(final ExtendedRequest r) throws NamingException {
        if (r == null) {
            return null;
        }
        final byte[] valueBytes = r.getEncodedValue();
        ASN1OctetString value;
        if (valueBytes == null) {
            value = null;
        }
        else {
            try {
                value = ASN1OctetString.decodeAsOctetString(valueBytes);
            }
            catch (final ASN1Exception ae) {
                throw new NamingException(StaticUtils.getExceptionMessage(ae));
            }
        }
        return new com.unboundid.ldap.sdk.ExtendedRequest(r.getID(), value);
    }
    
    @Override
    public String toString() {
        return this.r.toString();
    }
}
