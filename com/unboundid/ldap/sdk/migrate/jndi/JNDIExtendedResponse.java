package com.unboundid.ldap.sdk.migrate.jndi;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import javax.naming.NamingException;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.naming.ldap.ExtendedResponse;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JNDIExtendedResponse implements ExtendedResponse
{
    private static final long serialVersionUID = -9210853181740736844L;
    private final ExtendedResult r;
    
    public JNDIExtendedResponse(final ExtendedResult r) {
        this.r = r;
    }
    
    public JNDIExtendedResponse(final ExtendedResponse r) throws NamingException {
        this(toSDKExtendedResult(r));
    }
    
    JNDIExtendedResponse(final String id, final byte[] berValue, final int offset, final int length) throws NamingException {
        ASN1OctetString value;
        if (berValue == null) {
            value = null;
        }
        else {
            try {
                if (offset == 0 && length == berValue.length) {
                    value = ASN1OctetString.decodeAsOctetString(berValue);
                }
                else {
                    final byte[] valueBytes = new byte[length];
                    System.arraycopy(berValue, offset, valueBytes, 0, length);
                    value = ASN1OctetString.decodeAsOctetString(valueBytes);
                }
            }
            catch (final ASN1Exception ae) {
                throw new NamingException(StaticUtils.getExceptionMessage(ae));
            }
        }
        this.r = new ExtendedResult(-1, ResultCode.SUCCESS, null, null, null, id, value, null);
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
    
    public ExtendedResult toSDKExtendedResult() {
        return this.r;
    }
    
    public static ExtendedResult toSDKExtendedResult(final ExtendedResponse r) throws NamingException {
        if (r == null) {
            return null;
        }
        final byte[] encodedValue = r.getEncodedValue();
        JNDIExtendedResponse response;
        if (encodedValue == null) {
            response = new JNDIExtendedResponse(r.getID(), null, 0, 0);
        }
        else {
            response = new JNDIExtendedResponse(r.getID(), encodedValue, 0, encodedValue.length);
        }
        return response.toSDKExtendedResult();
    }
    
    @Override
    public String toString() {
        return this.r.toString();
    }
}
