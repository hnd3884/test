package com.unboundid.ldap.protocol;

import com.unboundid.ldap.sdk.Control;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1Buffer;
import java.util.Iterator;
import java.util.Collection;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.ldap.sdk.AddRequest;
import java.util.Collections;
import com.unboundid.ldap.sdk.Attribute;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AddRequestProtocolOp implements ProtocolOp
{
    private static final long serialVersionUID = -1195296296055518601L;
    private final List<Attribute> attributes;
    private final String dn;
    
    public AddRequestProtocolOp(final String dn, final List<Attribute> attributes) {
        this.dn = dn;
        this.attributes = Collections.unmodifiableList((List<? extends Attribute>)attributes);
    }
    
    public AddRequestProtocolOp(final AddRequest request) {
        this.dn = request.getDN();
        this.attributes = request.getAttributes();
    }
    
    AddRequestProtocolOp(final ASN1StreamReader reader) throws LDAPException {
        try {
            reader.beginSequence();
            Validator.ensureNotNull(this.dn = reader.readString());
            final ArrayList<Attribute> attrs = new ArrayList<Attribute>(10);
            final ASN1StreamReaderSequence attrSequence = reader.beginSequence();
            while (attrSequence.hasMoreElements()) {
                attrs.add(Attribute.readFrom(reader));
            }
            this.attributes = Collections.unmodifiableList((List<? extends Attribute>)attrs);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_ADD_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    public String getDN() {
        return this.dn;
    }
    
    public List<Attribute> getAttributes() {
        return this.attributes;
    }
    
    @Override
    public byte getProtocolOpType() {
        return 104;
    }
    
    @Override
    public ASN1Element encodeProtocolOp() {
        final ArrayList<ASN1Element> attrElements = new ArrayList<ASN1Element>(this.attributes.size());
        for (final Attribute a : this.attributes) {
            attrElements.add(a.encode());
        }
        return new ASN1Sequence((byte)104, new ASN1Element[] { new ASN1OctetString(this.dn), new ASN1Sequence(attrElements) });
    }
    
    public static AddRequestProtocolOp decodeProtocolOp(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final String dn = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
            final ASN1Element[] attrElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
            final ArrayList<Attribute> attributes = new ArrayList<Attribute>(attrElements.length);
            for (final ASN1Element ae : attrElements) {
                attributes.add(Attribute.decode(ASN1Sequence.decodeAsSequence(ae)));
            }
            return new AddRequestProtocolOp(dn, attributes);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ProtocolMessages.ERR_ADD_REQUEST_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void writeTo(final ASN1Buffer buffer) {
        final ASN1BufferSequence opSequence = buffer.beginSequence((byte)104);
        buffer.addOctetString(this.dn);
        final ASN1BufferSequence attrSequence = buffer.beginSequence();
        for (final Attribute a : this.attributes) {
            a.writeTo(buffer);
        }
        attrSequence.end();
        opSequence.end();
    }
    
    public AddRequest toAddRequest(final Control... controls) {
        return new AddRequest(this.dn, this.attributes, controls);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AddRequestProtocolOp(dn='");
        buffer.append(this.dn);
        buffer.append("', attrs={");
        final Iterator<Attribute> iterator = this.attributes.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append("})");
    }
}
