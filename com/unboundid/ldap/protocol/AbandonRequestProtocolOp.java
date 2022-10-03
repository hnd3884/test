package com.unboundid.ldap.protocol;

import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Element;
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
public final class AbandonRequestProtocolOp implements ProtocolOp
{
    private static final long serialVersionUID = -7824390696388231825L;
    private final int idToAbandon;
    
    public AbandonRequestProtocolOp(final int idToAbandon) {
        this.idToAbandon = idToAbandon;
    }
    
    AbandonRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            this.idToAbandon = reader.readInteger();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_ABANDON_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public int getIDToAbandon() {
        return this.idToAbandon;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 80;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        return new ASN1Integer((byte)80, this.idToAbandon);
    }
    
    public static AbandonRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            return new AbandonRequestProtocolOp(ASN1Integer.decodeAsInteger(element).intValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_ABANDON_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        buffer.addInteger((byte)80, this.idToAbandon);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AbandonRequestProtocolOp(idToAbandon=");
        buffer.append(this.idToAbandon);
        buffer.append(')');
    }
}
