package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.IntermediateResponse;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IntermediateResponseProtocolOp implements ProtocolOp
{
    public static final byte TYPE_OID = Byte.MIN_VALUE;
    public static final byte TYPE_VALUE = -127;
    private static final long serialVersionUID = 118549806265654465L;
    private final ASN1OctetString value;
    private final String oid;
    
    public IntermediateResponseProtocolOp(final String oid, final ASN1OctetString value) {
        this.oid = oid;
        if (value == null) {
            this.value = null;
        }
        else {
            this.value = new ASN1OctetString((byte)(-127), value.getValue());
        }
    }
    
    public IntermediateResponseProtocolOp(final IntermediateResponse response) {
        this.oid = response.getOID();
        final ASN1OctetString responseValue = response.getValue();
        if (responseValue == null) {
            this.value = null;
        }
        else {
            this.value = new ASN1OctetString((byte)(-127), responseValue.getValue());
        }
    }
    
    IntermediateResponseProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            final ASN1StreamReaderSequence opSequence = reader.beginSequence();
            String o = null;
            ASN1OctetString v = null;
            while (opSequence.hasMoreElements()) {
                final byte type = (byte)reader.peek();
                if (type == -128) {
                    o = reader.readString();
                }
                else {
                    if (type != -127) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_INTERMEDIATE_RESPONSE_INVALID_ELEMENT.get(StaticUtils.toHex(type)));
                    }
                    v = new ASN1OctetString(type, reader.readBytes());
                }
            }
            this.oid = o;
            this.value = v;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_INTERMEDIATE_RESPONSE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
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
        return 121;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(2);
        if (this.oid != null) {
            elements.add(new ASN1OctetString((byte)(-128), this.oid));
        }
        if (this.value != null) {
            elements.add(this.value);
        }
        return new ASN1Sequence((byte)121, elements);
    }
    
    public static IntermediateResponseProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            String oid = null;
            ASN1OctetString value = null;
            for (final ASN1Element e : ASN1Sequence.decodeAsSequence(element).elements()) {
                switch (e.getType()) {
                    case Byte.MIN_VALUE: {
                        oid = ASN1OctetString.decodeAsOctetString(e).stringValue();
                        break;
                    }
                    case -127: {
                        value = ASN1OctetString.decodeAsOctetString(e);
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_INTERMEDIATE_RESPONSE_INVALID_ELEMENT.get(StaticUtils.toHex(e.getType())));
                    }
                }
            }
            return new IntermediateResponseProtocolOp(oid, value);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_COMPARE_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)121);
        if (this.oid != null) {
            buffer.addOctetString((byte)(-128), this.oid);
        }
        if (this.value != null) {
            buffer.addElement(this.value);
        }
        opSequence.end();
    }
    
    public IntermediateResponse toIntermediateResponse(final Control... controls) {
        return new IntermediateResponse(-1, this.oid, this.value, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("IntermediateResponseProtocolOp(");
        if (this.oid != null) {
            buffer.append("oid='");
            buffer.append(this.oid);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
