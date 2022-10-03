package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CompareRequestProtocolOp implements ProtocolOp
{
    private static final long serialVersionUID = -562642367801440060L;
    private final ASN1OctetString assertionValue;
    private final String attributeName;
    private final String dn;
    
    public CompareRequestProtocolOp(final String dn, final String attributeName, final ASN1OctetString assertionValue) {
        this.dn = dn;
        this.attributeName = attributeName;
        this.assertionValue = assertionValue;
    }
    
    public CompareRequestProtocolOp(final CompareRequest request) {
        this.dn = request.getDN();
        this.attributeName = request.getAttributeName();
        this.assertionValue = request.getRawAssertionValue();
    }
    
    CompareRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            reader.beginSequence();
            this.dn = reader.readString();
            reader.beginSequence();
            this.attributeName = reader.readString();
            this.assertionValue = new ASN1OctetString(reader.readBytes());
            Validator.ensureNotNull(this.dn, this.attributeName, this.assertionValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_COMPARE_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getDN() {
        return this.dn;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public ASN1OctetString getAssertionValue() {
        return this.assertionValue;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 110;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        return new ASN1Sequence((byte)110, new ASN1Element[] { new ASN1OctetString(this.dn), new ASN1Sequence(new ASN1Element[] { new ASN1OctetString(this.attributeName), this.assertionValue }) });
    }
    
    public static CompareRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final String dn = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            final ASN1Element[] avaElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
            final String attributeName = ASN1OctetString.decodeAsOctetString(avaElements[0]).stringValue();
            final ASN1OctetString assertionValue = ASN1OctetString.decodeAsOctetString(avaElements[1]);
            return new CompareRequestProtocolOp(dn, attributeName, assertionValue);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_COMPARE_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)110);
        buffer.addOctetString(this.dn);
        final ASN1BufferSequence avaSequence = buffer.beginSequence();
        buffer.addOctetString(this.attributeName);
        buffer.addElement(this.assertionValue);
        avaSequence.end();
        opSequence.end();
    }
    
    public CompareRequest toCompareRequest(final Control... controls) {
        return new CompareRequest(this.dn, this.attributeName, this.assertionValue.getValue(), controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("CompareRequestProtocolOp(dn='");
        buffer.append(this.dn);
        buffer.append("', attributeName='");
        buffer.append(this.attributeName);
        buffer.append("', assertionValue='");
        buffer.append(this.assertionValue.stringValue());
        buffer.append("')");
    }
}
