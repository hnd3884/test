package com.unboundid.ldap.sdk.controls;

import com.unboundid.ldap.sdk.SearchResult;
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
public final class SimplePagedResultsControl extends Control implements DecodeableControl
{
    public static final String PAGED_RESULTS_OID = "1.2.840.113556.1.4.319";
    private static final long serialVersionUID = 2186787148024999291L;
    private final ASN1OctetString cookie;
    private final int size;
    
    SimplePagedResultsControl() {
        this.size = 0;
        this.cookie = new ASN1OctetString();
    }
    
    public SimplePagedResultsControl(final int pageSize) {
        super("1.2.840.113556.1.4.319", false, encodeValue(pageSize, null));
        this.size = pageSize;
        this.cookie = new ASN1OctetString();
    }
    
    public SimplePagedResultsControl(final int pageSize, final boolean isCritical) {
        super("1.2.840.113556.1.4.319", isCritical, encodeValue(pageSize, null));
        this.size = pageSize;
        this.cookie = new ASN1OctetString();
    }
    
    public SimplePagedResultsControl(final int pageSize, final ASN1OctetString cookie) {
        super("1.2.840.113556.1.4.319", false, encodeValue(pageSize, cookie));
        this.size = pageSize;
        if (cookie == null) {
            this.cookie = new ASN1OctetString();
        }
        else {
            this.cookie = cookie;
        }
    }
    
    public SimplePagedResultsControl(final int pageSize, final ASN1OctetString cookie, final boolean isCritical) {
        super("1.2.840.113556.1.4.319", isCritical, encodeValue(pageSize, cookie));
        this.size = pageSize;
        if (cookie == null) {
            this.cookie = new ASN1OctetString();
        }
        else {
            this.cookie = cookie;
        }
    }
    
    public SimplePagedResultsControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        super(oid, isCritical, value);
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PAGED_RESULTS_NO_VALUE.get());
        }
        ASN1Sequence valueSequence;
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            valueSequence = ASN1Sequence.decodeAsSequence(valueElement);
        }
        catch (final ASN1Exception ae) {
            Debug.debugException(ae);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PAGED_RESULTS_VALUE_NOT_SEQUENCE.get(ae), ae);
        }
        final ASN1Element[] valueElements = valueSequence.elements();
        if (valueElements.length != 2) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PAGED_RESULTS_INVALID_ELEMENT_COUNT.get(valueElements.length));
        }
        try {
            this.size = ASN1Integer.decodeAsInteger(valueElements[0]).intValue();
        }
        catch (final ASN1Exception ae2) {
            Debug.debugException(ae2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_PAGED_RESULTS_FIRST_NOT_INTEGER.get(ae2), ae2);
        }
        this.cookie = ASN1OctetString.decodeAsOctetString(valueElements[1]);
    }
    
    @Override
    public SimplePagedResultsControl decodeControl(final String oid, final boolean isCritical, final ASN1OctetString value) throws LDAPException {
        return new SimplePagedResultsControl(oid, isCritical, value);
    }
    
    public static SimplePagedResultsControl get(final SearchResult result) throws LDAPException {
        final Control c = result.getResponseControl("1.2.840.113556.1.4.319");
        if (c == null) {
            return null;
        }
        if (c instanceof SimplePagedResultsControl) {
            return (SimplePagedResultsControl)c;
        }
        return new SimplePagedResultsControl(c.getOID(), c.isCritical(), c.getValue());
    }
    
    private static ASN1OctetString encodeValue(final int pageSize, final ASN1OctetString cookie) {
        ASN1Element[] valueElements;
        if (cookie == null) {
            valueElements = new ASN1Element[] { new ASN1Integer(pageSize), new ASN1OctetString() };
        }
        else {
            valueElements = new ASN1Element[] { new ASN1Integer(pageSize), cookie };
        }
        return new ASN1OctetString(new ASN1Sequence(valueElements).encode());
    }
    
    public int getSize() {
        return this.size;
    }
    
    public ASN1OctetString getCookie() {
        return this.cookie;
    }
    
    public boolean moreResultsToReturn() {
        return this.cookie.getValue().length > 0;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_PAGED_RESULTS.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SimplePagedResultsControl(pageSize=");
        buffer.append(this.size);
        buffer.append(", isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
