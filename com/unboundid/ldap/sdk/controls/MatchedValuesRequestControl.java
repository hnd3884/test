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
public final class MatchedValuesRequestControl extends Control
{
    public static final String MATCHED_VALUES_REQUEST_OID = "1.2.826.0.1.3344810.2.3";
    private static final long serialVersionUID = 6799850686547208774L;
    private final MatchedValuesFilter[] filters;
    
    public MatchedValuesRequestControl(final MatchedValuesFilter... filters) {
        this(false, filters);
    }
    
    public MatchedValuesRequestControl(final List<MatchedValuesFilter> filters) {
        this(false, filters);
    }
    
    public MatchedValuesRequestControl(final boolean isCritical, final MatchedValuesFilter... filters) {
        super("1.2.826.0.1.3344810.2.3", isCritical, encodeValue(filters));
        this.filters = filters;
    }
    
    public MatchedValuesRequestControl(final boolean isCritical, final List<MatchedValuesFilter> filters) {
        this(isCritical, (MatchedValuesFilter[])filters.toArray(new MatchedValuesFilter[filters.size()]));
    }
    
    public MatchedValuesRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_REQUEST_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            final ASN1Element[] filterElements = ASN1Sequence.decodeAsSequence(valueElement).elements();
            this.filters = new MatchedValuesFilter[filterElements.length];
            for (int i = 0; i < filterElements.length; ++i) {
                this.filters[i] = MatchedValuesFilter.decode(filterElements[i]);
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_MV_REQUEST_CANNOT_DECODE.get(e), e);
        }
    }
    
    private static ASN1OctetString encodeValue(final MatchedValuesFilter[] filters) {
        Validator.ensureNotNull(filters);
        Validator.ensureTrue(filters.length > 0, "MatchedValuesRequestControl.filters must not be empty.");
        final ASN1Element[] elements = new ASN1Element[filters.length];
        for (int i = 0; i < filters.length; ++i) {
            elements[i] = filters[i].encode();
        }
        return new ASN1OctetString(new ASN1Sequence(elements).encode());
    }
    
    public MatchedValuesFilter[] getFilters() {
        return this.filters;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_MATCHED_VALUES_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("MatchedValuesRequestControl(filters={");
        for (int i = 0; i < this.filters.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append('\'');
            this.filters[i].toString(buffer);
            buffer.append('\'');
        }
        buffer.append("}, isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
