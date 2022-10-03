package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.Validator;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
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
public final class GetEffectiveRightsRequestControl extends Control
{
    public static final String GET_EFFECTIVE_RIGHTS_REQUEST_OID = "1.3.6.1.4.1.42.2.27.9.5.2";
    private static final long serialVersionUID = 354733122036206073L;
    private final String authzID;
    private final String[] attributes;
    
    public GetEffectiveRightsRequestControl(final String authzID, final String... attributes) {
        this(false, authzID, attributes);
    }
    
    public GetEffectiveRightsRequestControl(final boolean isCritical, final String authzID, final String... attributes) {
        super("1.3.6.1.4.1.42.2.27.9.5.2", isCritical, encodeValue(authzID, attributes));
        this.authzID = authzID;
        this.attributes = attributes;
    }
    
    public GetEffectiveRightsRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GER_REQUEST_NO_VALUE.get());
        }
        ASN1Element[] elements;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GER_REQUEST_VALUE_NOT_SEQUENCE.get(e), e);
        }
        if (elements.length < 1 || elements.length > 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GER_REQUEST_INVALID_ELEMENT_COUNT.get(elements.length));
        }
        this.authzID = ASN1OctetString.decodeAsOctetString(elements[0]).stringValue();
        if (elements.length == 2) {
            try {
                final ASN1Element[] attrElements = ASN1Sequence.decodeAsSequence(elements[1]).elements();
                this.attributes = new String[attrElements.length];
                for (int i = 0; i < attrElements.length; ++i) {
                    this.attributes[i] = ASN1OctetString.decodeAsOctetString(attrElements[i]).stringValue();
                }
                return;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_GER_REQUEST_CANNOT_DECODE.get(e), e);
            }
        }
        this.attributes = StaticUtils.NO_STRINGS;
    }
    
    private static ASN1OctetString encodeValue(final String authzID, final String[] attributes) {
        Validator.ensureNotNull(authzID);
        ASN1Element[] elements;
        if (attributes == null || attributes.length == 0) {
            elements = new ASN1Element[] { new ASN1OctetString(authzID), new ASN1Sequence() };
        }
        else {
            final ASN1Element[] attrElements = new ASN1Element[attributes.length];
            for (int i = 0; i < attributes.length; ++i) {
                attrElements[i] = new ASN1OctetString(attributes[i]);
            }
            elements = new ASN1Element[] { new ASN1OctetString(authzID), new ASN1Sequence(attrElements) };
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public String getAuthzID() {
        return this.authzID;
    }
    
    public String[] getAttributes() {
        return this.attributes;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_GET_EFFECTIVE_RIGHTS_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("GetEffectiveRightsRequestControl(authzId='");
        buffer.append(this.authzID);
        buffer.append('\'');
        if (this.attributes.length > 0) {
            buffer.append(", attributes={");
            for (int i = 0; i < this.attributes.length; ++i) {
                if (i > 0) {
                    buffer.append(", ");
                }
                buffer.append(this.attributes[i]);
            }
            buffer.append('}');
        }
        buffer.append(", isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
