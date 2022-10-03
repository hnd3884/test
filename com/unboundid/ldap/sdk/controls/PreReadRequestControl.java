package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PreReadRequestControl extends Control
{
    public static final String PRE_READ_REQUEST_OID = "1.3.6.1.1.13.1";
    private static final String[] NO_ATTRIBUTES;
    private static final long serialVersionUID = 1205235290978028739L;
    private final String[] attributes;
    
    public PreReadRequestControl(final String... attributes) {
        this(true, attributes);
    }
    
    public PreReadRequestControl(final boolean isCritical, final String... attributes) {
        super("1.3.6.1.1.13.1", isCritical, encodeValue(attributes));
        if (attributes == null) {
            this.attributes = PreReadRequestControl.NO_ATTRIBUTES;
        }
        else {
            this.attributes = attributes;
        }
    }
    
    public PreReadRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PRE_READ_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] attrElements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            this.attributes = new String[attrElements.length];
            for (int i = 0; i < attrElements.length; ++i) {
                this.attributes[i] = ASN1OctetString.decodeAsOctetString(attrElements[i]).stringValue();
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PRE_READ_REQUEST_CANNOT_DECODE.get(e), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final String[] attributes) {
        if (attributes == null || attributes.length == 0) {
            return new ASN1OctetString(new ASN1Sequence().encode());
        }
        final ASN1OctetString[] elements = new ASN1OctetString[attributes.length];
        for (int i = 0; i < attributes.length; ++i) {
            elements[i] = new ASN1OctetString(attributes[i]);
        }
        return new ASN1OctetString(new ASN1Sequence((ASN1Element[])elements).encode());
    }
    
    public String[] getAttributes() {
        return this.attributes;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PRE_READ_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PreReadRequestControl(attributes={");
        for (int i = 0; i < this.attributes.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append('\'');
            buffer.append(this.attributes[i]);
            buffer.append('\'');
        }
        buffer.append("}, isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
    
    static {
        NO_ATTRIBUTES = StaticUtils.NO_STRINGS;
    }
}
