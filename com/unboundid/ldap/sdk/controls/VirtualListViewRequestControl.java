package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.Validator;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class VirtualListViewRequestControl extends Control
{
    public static final String VIRTUAL_LIST_VIEW_REQUEST_OID = "2.16.840.1.113730.3.4.9";
    private static final byte TARGET_TYPE_OFFSET = -96;
    private static final byte TARGET_TYPE_GREATER_OR_EQUAL = -127;
    private static final long serialVersionUID = 4348423177859960815L;
    private final ASN1OctetString assertionValue;
    private final ASN1OctetString contextID;
    private final int afterCount;
    private final int beforeCount;
    private final int contentCount;
    private final int targetOffset;
    
    public VirtualListViewRequestControl(final int targetOffset, final int beforeCount, final int afterCount, final int contentCount, final ASN1OctetString contextID) {
        this(targetOffset, beforeCount, afterCount, contentCount, contextID, true);
    }
    
    public VirtualListViewRequestControl(final String assertionValue, final int beforeCount, final int afterCount, final ASN1OctetString contextID) {
        this(new ASN1OctetString(assertionValue), beforeCount, afterCount, contextID, true);
    }
    
    public VirtualListViewRequestControl(final byte[] assertionValue, final int beforeCount, final int afterCount, final ASN1OctetString contextID) {
        this(new ASN1OctetString(assertionValue), beforeCount, afterCount, contextID, true);
    }
    
    public VirtualListViewRequestControl(final ASN1OctetString assertionValue, final int beforeCount, final int afterCount, final ASN1OctetString contextID) {
        this(assertionValue, beforeCount, afterCount, contextID, true);
    }
    
    public VirtualListViewRequestControl(final int targetOffset, final int beforeCount, final int afterCount, final int contentCount, final ASN1OctetString contextID, final boolean isCritical) {
        super("2.16.840.1.113730.3.4.9", isCritical, encodeValue(targetOffset, beforeCount, afterCount, contentCount, contextID));
        this.targetOffset = targetOffset;
        this.beforeCount = beforeCount;
        this.afterCount = afterCount;
        this.contentCount = contentCount;
        this.contextID = contextID;
        this.assertionValue = null;
    }
    
    public VirtualListViewRequestControl(final String assertionValue, final int beforeCount, final int afterCount, final ASN1OctetString contextID, final boolean isCritical) {
        this(new ASN1OctetString(assertionValue), beforeCount, afterCount, contextID, isCritical);
    }
    
    public VirtualListViewRequestControl(final byte[] assertionValue, final int beforeCount, final int afterCount, final ASN1OctetString contextID, final boolean isCritical) {
        this(new ASN1OctetString(assertionValue), beforeCount, afterCount, contextID, isCritical);
    }
    
    public VirtualListViewRequestControl(final ASN1OctetString assertionValue, final int beforeCount, final int afterCount, final ASN1OctetString contextID, final boolean isCritical) {
        super("2.16.840.1.113730.3.4.9", isCritical, encodeValue(assertionValue, beforeCount, afterCount, contextID));
        this.assertionValue = assertionValue;
        this.beforeCount = beforeCount;
        this.afterCount = afterCount;
        this.contextID = contextID;
        this.targetOffset = -1;
        this.contentCount = -1;
    }
    
    public VirtualListViewRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            this.beforeCount = ASN1Integer.decodeAsInteger(elements[0]).intValue();
            this.afterCount = ASN1Integer.decodeAsInteger(elements[1]).intValue();
            switch (elements[2].getType()) {
                case -96: {
                    this.assertionValue = null;
                    final ASN1Element[] offsetElements = ASN1Sequence.decodeAsSequence(elements[2]).elements();
                    this.targetOffset = ASN1Integer.decodeAsInteger(offsetElements[0]).intValue();
                    this.contentCount = ASN1Integer.decodeAsInteger(offsetElements[1]).intValue();
                    break;
                }
                case -127: {
                    this.assertionValue = ASN1OctetString.decodeAsOctetString(elements[2]);
                    this.targetOffset = -1;
                    this.contentCount = -1;
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_REQUEST_INVALID_ELEMENT_TYPE.get(StaticUtils.toHex(elements[2].getType())));
                }
            }
            if (elements.length == 4) {
                this.contextID = ASN1OctetString.decodeAsOctetString(elements[3]);
            }
            else {
                this.contextID = null;
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_REQUEST_CANNOT_DECODE.get(e), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final int targetOffset, final int beforeCount, final int afterCount, final int contentCount, final ASN1OctetString contextID) {
        final ASN1Element[] targetElements = { new ASN1Integer(targetOffset), new ASN1Integer(contentCount) };
        ASN1Element[] vlvElements;
        if (contextID == null) {
            vlvElements = new ASN1Element[] { new ASN1Integer(beforeCount), new ASN1Integer(afterCount), new ASN1Sequence((byte)(-96), targetElements) };
        }
        else {
            vlvElements = new ASN1Element[] { new ASN1Integer(beforeCount), new ASN1Integer(afterCount), new ASN1Sequence((byte)(-96), targetElements), contextID };
        }
        return new ASN1OctetString(new ASN1Sequence(vlvElements).encode());
    }
    
    private static ASN1OctetString encodeValue(final ASN1OctetString assertionValue, final int beforeCount, final int afterCount, final ASN1OctetString contextID) {
        Validator.ensureNotNull(assertionValue);
        ASN1Element[] vlvElements;
        if (contextID == null) {
            vlvElements = new ASN1Element[] { new ASN1Integer(beforeCount), new ASN1Integer(afterCount), new ASN1OctetString((byte)(-127), assertionValue.getValue()) };
        }
        else {
            vlvElements = new ASN1Element[] { new ASN1Integer(beforeCount), new ASN1Integer(afterCount), new ASN1OctetString((byte)(-127), assertionValue.getValue()), contextID };
        }
        return new ASN1OctetString(new ASN1Sequence(vlvElements).encode());
    }
    
    public int getTargetOffset() {
        return this.targetOffset;
    }
    
    public String getAssertionValueString() {
        if (this.assertionValue == null) {
            return null;
        }
        return this.assertionValue.stringValue();
    }
    
    public byte[] getAssertionValueBytes() {
        if (this.assertionValue == null) {
            return null;
        }
        return this.assertionValue.getValue();
    }
    
    public ASN1OctetString getAssertionValue() {
        return this.assertionValue;
    }
    
    public int getBeforeCount() {
        return this.beforeCount;
    }
    
    public int getAfterCount() {
        return this.afterCount;
    }
    
    public int getContentCount() {
        return this.contentCount;
    }
    
    public ASN1OctetString getContextID() {
        return this.contextID;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_VLV_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("VirtualListViewRequestControl(beforeCount=");
        buffer.append(this.beforeCount);
        buffer.append(", afterCount=");
        buffer.append(this.afterCount);
        if (this.assertionValue == null) {
            buffer.append(", targetOffset=");
            buffer.append(this.targetOffset);
            buffer.append(", contentCount=");
            buffer.append(this.contentCount);
        }
        else {
            buffer.append(", assertionValue='");
            buffer.append(this.assertionValue.stringValue());
            buffer.append('\'');
        }
        buffer.append(", isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
