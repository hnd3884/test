package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class VirtualListViewResponseControl extends Control implements DecodeableControl
{
    public static final String VIRTUAL_LIST_VIEW_RESPONSE_OID = "2.16.840.1.113730.3.4.10";
    private static final long serialVersionUID = -534656674756287217L;
    private final ASN1OctetString contextID;
    private final int contentCount;
    private final ResultCode resultCode;
    private final int targetPosition;
    
    VirtualListViewResponseControl() {
        this.targetPosition = -1;
        this.contentCount = -1;
        this.resultCode = null;
        this.contextID = null;
    }
    
    public VirtualListViewResponseControl(final int targetPosition, final int contentCount, final ResultCode resultCode, final ASN1OctetString contextID) {
        super("2.16.840.1.113730.3.4.10", false, encodeValue(targetPosition, contentCount, resultCode, contextID));
        this.targetPosition = targetPosition;
        this.contentCount = contentCount;
        this.resultCode = resultCode;
        this.contextID = contextID;
    }
    
    public VirtualListViewResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_RESPONSE_NO_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_RESPONSE_VALUE_NOT_SEQUENCE.get(ae), ae);
        }
        final ASN1Element[] valueElements = valueSequence.elements();
        if (valueElements.length < 3 || valueElements.length > 4) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_RESPONSE_INVALID_ELEMENT_COUNT.get(valueElements.length));
        }
        try {
            this.targetPosition = ASN1Integer.decodeAsInteger(valueElements[0]).intValue();
        }
        catch (final ASN1Exception ae2) {
            Debug.debugException(ae2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_RESPONSE_FIRST_NOT_INTEGER.get(ae2), ae2);
        }
        try {
            this.contentCount = ASN1Integer.decodeAsInteger(valueElements[1]).intValue();
        }
        catch (final ASN1Exception ae2) {
            Debug.debugException(ae2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_RESPONSE_SECOND_NOT_INTEGER.get(ae2), ae2);
        }
        try {
            final int rc = ASN1Enumerated.decodeAsEnumerated(valueElements[2]).intValue();
            this.resultCode = ResultCode.valueOf(rc);
        }
        catch (final ASN1Exception ae2) {
            Debug.debugException(ae2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_VLV_RESPONSE_THIRD_NOT_ENUM.get(ae2), ae2);
        }
        if (valueElements.length == 4) {
            this.contextID = ASN1OctetString.decodeAsOctetString(valueElements[3]);
        }
        else {
            this.contextID = null;
        }
    }
    
    @Override
    public VirtualListViewResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new VirtualListViewResponseControl(oid, isCritical, value);
    }
    
    public static VirtualListViewResponseControl get(final SearchResult result) throws LDAPException {
        final Control c = result.getResponseControl("2.16.840.1.113730.3.4.10");
        if (c == null) {
            return null;
        }
        if (c instanceof VirtualListViewResponseControl) {
            return (VirtualListViewResponseControl)c;
        }
        return new VirtualListViewResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    private static ASN1OctetString encodeValue(final int targetPosition, final int contentCount, final ResultCode resultCode, final ASN1OctetString contextID) {
        ASN1Element[] vlvElements;
        if (contextID == null) {
            vlvElements = new ASN1Element[] { new ASN1Integer(targetPosition), new ASN1Integer(contentCount), new ASN1Enumerated(resultCode.intValue()) };
        }
        else {
            vlvElements = new ASN1Element[] { new ASN1Integer(targetPosition), new ASN1Integer(contentCount), new ASN1Enumerated(resultCode.intValue()), contextID };
        }
        return new ASN1OctetString(new ASN1Sequence(vlvElements).encode());
    }
    
    public int getTargetPosition() {
        return this.targetPosition;
    }
    
    public int getContentCount() {
        return this.contentCount;
    }
    
    public ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public ASN1OctetString getContextID() {
        return this.contextID;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_VLV_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("VirtualListViewResponseControl(targetPosition=");
        buffer.append(this.targetPosition);
        buffer.append(", contentCount=");
        buffer.append(this.contentCount);
        buffer.append(", resultCode=");
        buffer.append(this.resultCode);
        buffer.append(')');
    }
}
