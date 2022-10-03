package com.unboundid.ldap.sdk.controls;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import com.unboundid.ldap.sdk.Attribute;
import java.util.ArrayList;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import com.unboundid.ldap.sdk.Control;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AssertionRequestControl extends Control
{
    public static final String ASSERTION_REQUEST_OID = "1.3.6.1.1.12";
    private static final long serialVersionUID = 6592634203410511095L;
    private final Filter filter;
    
    public AssertionRequestControl(final String filter) throws LDAPException {
        this(Filter.create(filter), true);
    }
    
    public AssertionRequestControl(final Filter filter) {
        this(filter, true);
    }
    
    public AssertionRequestControl(final String filter, final boolean isCritical) throws LDAPException {
        this(Filter.create(filter), isCritical);
    }
    
    public AssertionRequestControl(final Filter filter, final boolean isCritical) {
        super("1.3.6.1.1.12", isCritical, encodeValue(filter));
        this.filter = filter;
    }
    
    public AssertionRequestControl(final Control control) throws LDAPException {
        super(control);
        final ASN1OctetString value = control.getValue();
        if (value == null) {
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSERT_NO_VALUE.get());
        }
        try {
            final ASN1Element valueElement = ASN1Element.decode(value.getValue());
            this.filter = Filter.decode(valueElement);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_ASSERT_CANNOT_DECODE.get(e), e);
        }
    }
    
    public static AssertionRequestControl generate(final Entry sourceEntry, final String... attributes) {
        Validator.ensureNotNull(sourceEntry);
        ArrayList<Filter> andComponents;
        if (attributes == null || attributes.length == 0) {
            final Collection<Attribute> entryAttrs = sourceEntry.getAttributes();
            andComponents = new ArrayList<Filter>(entryAttrs.size());
            for (final Attribute a : entryAttrs) {
                for (final ASN1OctetString v : a.getRawValues()) {
                    andComponents.add(Filter.createEqualityFilter(a.getName(), v.getValue()));
                }
            }
        }
        else {
            andComponents = new ArrayList<Filter>(attributes.length);
            for (final String name : attributes) {
                final Attribute a2 = sourceEntry.getAttribute(name);
                if (a2 != null) {
                    for (final ASN1OctetString v2 : a2.getRawValues()) {
                        andComponents.add(Filter.createEqualityFilter(name, v2.getValue()));
                    }
                }
            }
        }
        if (andComponents.size() == 1) {
            return new AssertionRequestControl(andComponents.get(0));
        }
        return new AssertionRequestControl(Filter.createANDFilter(andComponents));
    }
    
    private static ASN1OctetString encodeValue(final Filter filter) {
        return new ASN1OctetString(filter.encode().encode());
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    @Override
    public String getControlName() {
        return ControlMessages.INFO_CONTROL_NAME_ASSERTION_REQUEST.get();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AssertionRequestControl(filter='");
        this.filter.toString(buffer);
        buffer.append("', isCritical=");
        buffer.append(this.isCritical());
        buffer.append(')');
    }
}
