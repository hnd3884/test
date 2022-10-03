package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ExtendedRequestProtocolOp implements ProtocolOp
{
    public static final byte TYPE_OID = Byte.MIN_VALUE;
    public static final byte TYPE_VALUE = -127;
    private static final long serialVersionUID = -5343424210200494377L;
    private final ASN1OctetString value;
    private final String oid;
    
    public ExtendedRequestProtocolOp(final String oid, final ASN1OctetString value) {
        this.oid = oid;
        if (value == null) {
            this.value = null;
        }
        else {
            this.value = new ASN1OctetString((byte)(-127), value.getValue());
        }
    }
    
    public ExtendedRequestProtocolOp(final ExtendedRequest request) {
        this.oid = request.getOID();
        this.value = request.getValue();
    }
    
    ExtendedRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            final ASN1StreamReaderSequence opSequence = reader.beginSequence();
            Validator.ensureNotNull(this.oid = reader.readString());
            if (opSequence.hasMoreElements()) {
                this.value = new ASN1OctetString((byte)(-127), reader.readBytes());
            }
            else {
                this.value = null;
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_EXTENDED_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getOID() {
        return this.oid;
    }
    
    public ASN1OctetString getValue() {
        return this.value;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 119;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        if (this.value == null) {
            return new ASN1Sequence((byte)119, new ASN1Element[] { new ASN1OctetString((byte)(-128), this.oid) });
        }
        return new ASN1Sequence((byte)119, new ASN1Element[] { new ASN1OctetString((byte)(-128), this.oid), this.value });
    }
    
    public static ExtendedRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final String oid = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            ASN1OctetString value;
            if (elements.length == 1) {
                value = null;
            }
            else {
                value = ASN1OctetString.decodeAsOctetString(elements[1]);
            }
            return new ExtendedRequestProtocolOp(oid, value);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_EXTENDED_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)119);
        buffer.addOctetString((byte)(-128), this.oid);
        if (this.value != null) {
            buffer.addOctetString((byte)(-127), this.value.getValue());
        }
        opSequence.end();
    }
    
    public ExtendedRequest toExtendedRequest(final Control... controls) {
        return new ExtendedRequest(this.oid, this.value, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ExtendedRequestProtocolOp(oid='");
        buffer.append(this.oid);
        buffer.append("')");
    }
}
