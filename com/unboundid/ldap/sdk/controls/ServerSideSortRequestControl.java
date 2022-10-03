package com.unboundid.ldap.sdk.controls;

import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ServerSideSortRequestControl extends Control
{
    public static final String SERVER_SIDE_SORT_REQUEST_OID = "1.2.840.113556.1.4.473";
    private static final long serialVersionUID = -3021901578330574772L;
    private final SortKey[] sortKeys;
    
    public ServerSideSortRequestControl(final SortKey... sortKeys) {
        this(false, sortKeys);
    }
    
    public ServerSideSortRequestControl(final List<SortKey> sortKeys) {
        this(false, sortKeys);
    }
    
    public ServerSideSortRequestControl(final boolean isCritical, final SortKey... sortKeys) {
        super("1.2.840.113556.1.4.473", isCritical, encodeValue(sortKeys));
        this.sortKeys = sortKeys;
    }
    
    public ServerSideSortRequestControl(final boolean isCritical, final List<SortKey> sortKeys) {
        this(isCritical, (SortKey[])sortKeys.toArray(new SortKey[sortKeys.size()]));
    }
    
    public ServerSideSortRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            this.sortKeys = new SortKey[elements.length];
            for (int i = 0; i < elements.length; ++i) {
                this.sortKeys[i] = SortKey.decode(elements[i]);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_SORT_REQUEST_CANNOT_DECODE.get(e), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final SortKey[] sortKeys) {
        Validator.ensureNotNull(sortKeys);
        Validator.ensureTrue(sortKeys.length > 0, "ServerSideSortRequestControl.sortKeys must not be empty.");
        final ASN1Element[] valueElements = new ASN1Element[sortKeys.length];
        for (int i = 0; i < sortKeys.length; ++i) {
            valueElements[i] = sortKeys[i].encode();
        }
        return new ASN1OctetString(new ASN1Sequence(valueElements).encode());
    }
    
    public SortKey[] getSortKeys() {
        return this.sortKeys;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_SORT_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ServerSideSortRequestControl(sortKeys={");
        for (int i = 0; i < this.sortKeys.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append('\'');
            this.sortKeys[i].toString(buffer);
            buffer.append('\'');
        }
        buffer.append("})");
    }
}
