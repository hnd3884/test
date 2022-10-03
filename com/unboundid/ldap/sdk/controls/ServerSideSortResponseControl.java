package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.asn1.ASN1Enumerated;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ServerSideSortResponseControl extends Control implements DecodeableControl
{
    public static final String SERVER_SIDE_SORT_RESPONSE_OID = "1.2.840.113556.1.4.474";
    private static final byte TYPE_ATTRIBUTE_TYPE = Byte.MIN_VALUE;
    private static final long serialVersionUID = -8707533262822875822L;
    private final ResultCode resultCode;
    private final String attributeName;
    
    ServerSideSortResponseControl() {
        this.resultCode = null;
        this.attributeName = null;
    }
    
    public ServerSideSortResponseControl(final ResultCode resultCode, final String attributeName) {
        this(resultCode, attributeName, false);
    }
    
    public ServerSideSortResponseControl(final ResultCode resultCode, final String attributeName, final boolean isCritical) {
        super("1.2.840.113556.1.4.474", isCritical, encodeValue(resultCode, attributeName));
        this.resultCode = resultCode;
        this.attributeName = attributeName;
    }
    
    public ServerSideSortResponseControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_RESPONSE_NO_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_RESPONSE_VALUE_NOT_SEQUENCE.get(ae), ae);
        }
        final ASN1Element[] valueElements = valueSequence.elements();
        if (valueElements.length < 1 || valueElements.length > 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_RESPONSE_INVALID_ELEMENT_COUNT.get(valueElements.length));
        }
        try {
            final int rc = ASN1Enumerated.decodeAsEnumerated(valueElements[0]).intValue();
            this.resultCode = ResultCode.valueOf(rc);
        }
        catch (final ASN1Exception ae2) {
            Debug.debugException(ae2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_RESPONSE_FIRST_NOT_ENUM.get(ae2), ae2);
        }
        if (valueElements.length == 2) {
            this.attributeName = ASN1OctetString.decodeAsOctetString(valueElements[1]).stringValue();
        }
        else {
            this.attributeName = null;
        }
    }
    
    @Override
    public ServerSideSortResponseControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new ServerSideSortResponseControl(oid, isCritical, value);
    }
    
    public static ServerSideSortResponseControl get(final SearchResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.2.840.113556.1.4.474");
        if (c == null) {
            return null;
        }
        if (c instanceof ServerSideSortResponseControl) {
            return (ServerSideSortResponseControl)c;
        }
        return new ServerSideSortResponseControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    private static ASN1OctetString encodeValue(final ResultCode resultCode, final String attributeName) {
        ASN1Element[] valueElements;
        if (attributeName == null) {
            valueElements = new ASN1Element[] { new ASN1Enumerated(resultCode.intValue()) };
        }
        else {
            valueElements = new ASN1Element[] { new ASN1Enumerated(resultCode.intValue()), new ASN1OctetString((byte)(-128), attributeName) };
        }
        return new ASN1OctetString(new ASN1Sequence(valueElements).encode());
    }
    
    public ResultCode getResultCode() {
        return this.resultCode;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SORT_RESPONSE.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ServerSideSortResponseControl(resultCode=");
        buffer.append(this.resultCode);
        if (this.attributeName != null) {
            buffer.append(", attributeName='");
            buffer.append(this.attributeName);
            buffer.append('\'');
        }
        buffer.append(')');
    }
}
