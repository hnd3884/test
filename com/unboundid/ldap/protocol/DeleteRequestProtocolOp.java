package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DeleteRequestProtocolOp implements ProtocolOp
{
    private static final long serialVersionUID = 1577020640104649789L;
    private final String dn;
    
    public DeleteRequestProtocolOp(final String dn) {
        this.dn = dn;
    }
    
    public DeleteRequestProtocolOp(final DeleteRequest request) {
        this.dn = request.getDN();
    }
    
    DeleteRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            Validator.ensureNotNull(this.dn = reader.readString());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_DELETE_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getDN() {
        return this.dn;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 74;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        return new ASN1OctetString((byte)74, this.dn);
    }
    
    public static DeleteRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            return new DeleteRequestProtocolOp(ASN1OctetString.decodeAsOctetString(element).stringValue());
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_DELETE_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        buffer.addOctetString((byte)74, this.dn);
    }
    
    public DeleteRequest toDeleteRequest(final Control... controls) {
        return new DeleteRequest(this.dn, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DeleteRequestProtocolOp(dn='");
        buffer.append(this.dn);
        buffer.append("')");
    }
}
