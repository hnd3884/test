package com.unboundid.ldap.protocol;

import com.unboundid.asn1.ASN1Null;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class UnbindRequestProtocolOp implements ProtocolOp
{
    private static final long serialVersionUID = 1703200292192488474L;
    
    public UnbindRequestProtocolOp() {
    }
    
    UnbindRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            reader.readNull();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_UNBIND_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public byte getProtocolOpType() {
        return 66;
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        buffer.addNull((byte)66);
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        return new ASN1Null((byte)66);
    }
    
    public static UnbindRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            ASN1Null.decodeAsNull(element);
            return new UnbindRequestProtocolOp();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_UNBIND_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("UnbindRequestProtocolOp()");
    }
}
