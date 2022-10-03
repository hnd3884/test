package com.unboundid.ldap.sdk.unboundidds.controls;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collection;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1Enumerated;
import java.util.ArrayList;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JoinRequestValue implements Serializable
{
    private static final String[] NO_ATTRIBUTES;
    private static final byte TYPE_SCOPE = Byte.MIN_VALUE;
    private static final byte TYPE_DEREF_POLICY = -127;
    private static final byte TYPE_SIZE_LIMIT = -126;
    private static final byte TYPE_FILTER = -93;
    private static final byte TYPE_ATTRIBUTES = -92;
    private static final byte TYPE_REQUIRE_MATCH = -123;
    private static final byte TYPE_NESTED_JOIN = -90;
    private static final long serialVersionUID = 4675881185117657177L;
    private final boolean requireMatch;
    private final DereferencePolicy derefPolicy;
    private final Filter filter;
    private final Integer sizeLimit;
    private final JoinBaseDN baseDN;
    private final JoinRequestValue nestedJoin;
    private final JoinRule joinRule;
    private final SearchScope scope;
    private final String[] attributes;
    
    public JoinRequestValue(final JoinRule joinRule, final JoinBaseDN baseDN, final SearchScope scope, final DereferencePolicy derefPolicy, final Integer sizeLimit, final Filter filter, final String[] attributes, final boolean requireMatch, final JoinRequestValue nestedJoin) {
        Validator.ensureNotNull(joinRule, baseDN);
        this.joinRule = joinRule;
        this.baseDN = baseDN;
        this.scope = scope;
        this.derefPolicy = derefPolicy;
        this.sizeLimit = sizeLimit;
        this.filter = filter;
        this.requireMatch = requireMatch;
        this.nestedJoin = nestedJoin;
        if (attributes == null) {
            this.attributes = JoinRequestValue.NO_ATTRIBUTES;
        }
        else {
            this.attributes = attributes;
        }
    }
    
    public JoinRule getJoinRule() {
        return this.joinRule;
    }
    
    public JoinBaseDN getBaseDN() {
        return this.baseDN;
    }
    
    public SearchScope getScope() {
        return this.scope;
    }
    
    public DereferencePolicy getDerefPolicy() {
        return this.derefPolicy;
    }
    
    public Integer getSizeLimit() {
        return this.sizeLimit;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public String[] getAttributes() {
        return this.attributes;
    }
    
    public boolean requireMatch() {
        return this.requireMatch;
    }
    
    public JoinRequestValue getNestedJoin() {
        return this.nestedJoin;
    }
    
    ASN1Element encode() {
        final ArrayList<ASN1Element> elements = new ArrayList<ASN1Element>(9);
        elements.add(this.joinRule.encode());
        elements.add(this.baseDN.encode());
        if (this.scope != null) {
            elements.add(new ASN1Enumerated((byte)(-128), this.scope.intValue()));
        }
        if (this.derefPolicy != null) {
            elements.add(new ASN1Enumerated((byte)(-127), this.derefPolicy.intValue()));
        }
        if (this.sizeLimit != null) {
            elements.add(new ASN1Integer((byte)(-126), this.sizeLimit));
        }
        if (this.filter != null) {
            elements.add(new ASN1OctetString((byte)(-93), this.filter.encode().encode()));
        }
        if (this.attributes != null && this.attributes.length > 0) {
            final ASN1Element[] attrElements = new ASN1Element[this.attributes.length];
            for (int i = 0; i < this.attributes.length; ++i) {
                attrElements[i] = new ASN1OctetString(this.attributes[i]);
            }
            elements.add(new ASN1Sequence((byte)(-92), attrElements));
        }
        if (this.requireMatch) {
            elements.add(new ASN1Boolean((byte)(-123), this.requireMatch));
        }
        if (this.nestedJoin != null) {
            elements.add(new ASN1OctetString((byte)(-90), this.nestedJoin.encode().getValue()));
        }
        return new ASN1Sequence(elements);
    }
    
    static JoinRequestValue decode(final ASN1Element element) throws LDAPException {
        try {
            final ASN1Element[] elements = ASN1Sequence.decodeAsSequence(element).elements();
            final JoinRule joinRule = JoinRule.decode(elements[0]);
            final JoinBaseDN baseDN = JoinBaseDN.decode(elements[1]);
            SearchScope scope = null;
            DereferencePolicy derefPolicy = null;
            Integer sizeLimit = null;
            Filter filter = null;
            String[] attributes = JoinRequestValue.NO_ATTRIBUTES;
            boolean requireMatch = false;
            JoinRequestValue nestedJoin = null;
            for (int i = 2; i < elements.length; ++i) {
                switch (elements[i].getType()) {
                    case Byte.MIN_VALUE: {
                        scope = SearchScope.valueOf(ASN1Enumerated.decodeAsEnumerated(elements[i]).intValue());
                        break;
                    }
                    case -127: {
                        derefPolicy = DereferencePolicy.valueOf(ASN1Enumerated.decodeAsEnumerated(elements[i]).intValue());
                        break;
                    }
                    case -126: {
                        sizeLimit = ASN1Integer.decodeAsInteger(elements[i]).intValue();
                        break;
                    }
                    case -93: {
                        filter = Filter.decode(ASN1Element.decode(elements[i].getValue()));
                        break;
                    }
                    case -92: {
                        final ASN1Element[] attrElements = ASN1Sequence.decodeAsSequence(elements[i]).elements();
                        final ArrayList<String> attrList = new ArrayList<String>(attrElements.length);
                        for (final ASN1Element e : attrElements) {
                            attrList.add(ASN1OctetString.decodeAsOctetString(e).stringValue());
                        }
                        attributes = new String[attrList.size()];
                        attrList.toArray(attributes);
                        break;
                    }
                    case -123: {
                        requireMatch = ASN1Boolean.decodeAsBoolean(elements[i]).booleanValue();
                        break;
                    }
                    case -90: {
                        nestedJoin = decode(elements[i]);
                        break;
                    }
                    default: {
                        throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_REQUEST_VALUE_INVALID_ELEMENT_TYPE.get(elements[i].getType()));
                    }
                }
            }
            return new JoinRequestValue(joinRule, baseDN, scope, derefPolicy, sizeLimit, filter, attributes, requireMatch, nestedJoin);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, ControlMessages.ERR_JOIN_REQUEST_VALUE_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("JoinRequestValue(joinRule=");
        this.joinRule.toString(buffer);
        buffer.append(", baseDN=");
        this.baseDN.toString(buffer);
        buffer.append(", scope=");
        buffer.append(String.valueOf(this.scope));
        buffer.append(", derefPolicy=");
        buffer.append(String.valueOf(this.derefPolicy));
        buffer.append(", sizeLimit=");
        buffer.append(this.sizeLimit);
        buffer.append(", filter=");
        if (this.filter == null) {
            buffer.append("null");
        }
        else {
            buffer.append('\'');
            this.filter.toString(buffer);
            buffer.append('\'');
        }
        buffer.append(", attributes={");
        for (int i = 0; i < this.attributes.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(this.attributes[i]);
        }
        buffer.append("}, requireMatch=");
        buffer.append(this.requireMatch);
        buffer.append(", nestedJoin=");
        if (this.nestedJoin == null) {
            buffer.append("null");
        }
        else {
            this.nestedJoin.toString(buffer);
        }
        buffer.append(')');
    }
    
    static {
        NO_ATTRIBUTES = StaticUtils.NO_STRINGS;
    }
}
