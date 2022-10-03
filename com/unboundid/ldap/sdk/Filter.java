package com.unboundid.ldap.sdk;

import java.util.HashSet;
import com.unboundid.ldap.matchingrules.CaseIgnoreStringMatchingRule;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.LinkedHashSet;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.asn1.ASN1StreamReaderSequence;
import com.unboundid.asn1.ASN1StreamReaderSet;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.util.Debug;
import com.unboundid.asn1.ASN1StreamReader;
import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.asn1.ASN1BufferSequence;
import com.unboundid.asn1.ASN1BufferSet;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Buffer;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ByteStringBuffer;
import java.util.Collection;
import java.util.List;
import com.unboundid.util.Validator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Filter implements Serializable
{
    public static final byte FILTER_TYPE_AND = -96;
    public static final byte FILTER_TYPE_OR = -95;
    public static final byte FILTER_TYPE_NOT = -94;
    public static final byte FILTER_TYPE_EQUALITY = -93;
    public static final byte FILTER_TYPE_SUBSTRING = -92;
    public static final byte FILTER_TYPE_GREATER_OR_EQUAL = -91;
    public static final byte FILTER_TYPE_LESS_OR_EQUAL = -90;
    public static final byte FILTER_TYPE_PRESENCE = -121;
    public static final byte FILTER_TYPE_APPROXIMATE_MATCH = -88;
    public static final byte FILTER_TYPE_EXTENSIBLE_MATCH = -87;
    private static final byte SUBSTRING_TYPE_SUBINITIAL = Byte.MIN_VALUE;
    private static final byte SUBSTRING_TYPE_SUBANY = -127;
    private static final byte SUBSTRING_TYPE_SUBFINAL = -126;
    private static final byte EXTENSIBLE_TYPE_MATCHING_RULE_ID = -127;
    private static final byte EXTENSIBLE_TYPE_ATTRIBUTE_NAME = -126;
    private static final byte EXTENSIBLE_TYPE_MATCH_VALUE = -125;
    private static final byte EXTENSIBLE_TYPE_DN_ATTRIBUTES = -124;
    private static final Filter[] NO_FILTERS;
    private static final ASN1OctetString[] NO_SUB_ANY;
    private static final long serialVersionUID = -2734184402804691970L;
    private final ASN1OctetString assertionValue;
    private final ASN1OctetString subFinal;
    private final ASN1OctetString subInitial;
    private final ASN1OctetString[] subAny;
    private final boolean dnAttributes;
    private final Filter notComp;
    private final Filter[] filterComps;
    private final byte filterType;
    private final String attrName;
    private volatile String filterString;
    private final String matchingRuleID;
    private volatile String normalizedString;
    
    private Filter(final String filterString, final byte filterType, final Filter[] filterComps, final Filter notComp, final String attrName, final ASN1OctetString assertionValue, final ASN1OctetString subInitial, final ASN1OctetString[] subAny, final ASN1OctetString subFinal, final String matchingRuleID, final boolean dnAttributes) {
        this.filterString = filterString;
        this.filterType = filterType;
        this.filterComps = filterComps;
        this.notComp = notComp;
        this.attrName = attrName;
        this.assertionValue = assertionValue;
        this.subInitial = subInitial;
        this.subAny = subAny;
        this.subFinal = subFinal;
        this.matchingRuleID = matchingRuleID;
        this.dnAttributes = dnAttributes;
    }
    
    public static Filter createANDFilter(final Filter... andComponents) {
        Validator.ensureNotNull(andComponents);
        return new Filter(null, (byte)(-96), andComponents, null, null, null, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createANDFilter(final List<Filter> andComponents) {
        Validator.ensureNotNull(andComponents);
        return new Filter(null, (byte)(-96), andComponents.toArray(new Filter[andComponents.size()]), null, null, null, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createANDFilter(final Collection<Filter> andComponents) {
        Validator.ensureNotNull(andComponents);
        return new Filter(null, (byte)(-96), andComponents.toArray(new Filter[andComponents.size()]), null, null, null, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createORFilter(final Filter... orComponents) {
        Validator.ensureNotNull(orComponents);
        return new Filter(null, (byte)(-95), orComponents, null, null, null, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createORFilter(final List<Filter> orComponents) {
        Validator.ensureNotNull(orComponents);
        return new Filter(null, (byte)(-95), orComponents.toArray(new Filter[orComponents.size()]), null, null, null, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createORFilter(final Collection<Filter> orComponents) {
        Validator.ensureNotNull(orComponents);
        return new Filter(null, (byte)(-95), orComponents.toArray(new Filter[orComponents.size()]), null, null, null, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createNOTFilter(final Filter notComponent) {
        Validator.ensureNotNull(notComponent);
        return new Filter(null, (byte)(-94), Filter.NO_FILTERS, notComponent, null, null, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createEqualityFilter(final String attributeName, final String assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-93), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createEqualityFilter(final String attributeName, final byte[] assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-93), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    static Filter createEqualityFilter(final String attributeName, final ASN1OctetString assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-93), Filter.NO_FILTERS, null, attributeName, assertionValue, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createSubstringFilter(final String attributeName, final String subInitial, final String[] subAny, final String subFinal) {
        Validator.ensureNotNull(attributeName);
        Validator.ensureTrue(subInitial != null || (subAny != null && subAny.length > 0) || subFinal != null);
        ASN1OctetString subInitialOS;
        if (subInitial == null) {
            subInitialOS = null;
        }
        else {
            subInitialOS = new ASN1OctetString(subInitial);
        }
        ASN1OctetString[] subAnyArray;
        if (subAny == null) {
            subAnyArray = Filter.NO_SUB_ANY;
        }
        else {
            subAnyArray = new ASN1OctetString[subAny.length];
            for (int i = 0; i < subAny.length; ++i) {
                subAnyArray[i] = new ASN1OctetString(subAny[i]);
            }
        }
        ASN1OctetString subFinalOS;
        if (subFinal == null) {
            subFinalOS = null;
        }
        else {
            subFinalOS = new ASN1OctetString(subFinal);
        }
        return new Filter(null, (byte)(-92), Filter.NO_FILTERS, null, attributeName, null, subInitialOS, subAnyArray, subFinalOS, null, false);
    }
    
    public static Filter createSubstringFilter(final String attributeName, final byte[] subInitial, final byte[][] subAny, final byte[] subFinal) {
        Validator.ensureNotNull(attributeName);
        Validator.ensureTrue(subInitial != null || (subAny != null && subAny.length > 0) || subFinal != null);
        ASN1OctetString subInitialOS;
        if (subInitial == null) {
            subInitialOS = null;
        }
        else {
            subInitialOS = new ASN1OctetString(subInitial);
        }
        ASN1OctetString[] subAnyArray;
        if (subAny == null) {
            subAnyArray = Filter.NO_SUB_ANY;
        }
        else {
            subAnyArray = new ASN1OctetString[subAny.length];
            for (int i = 0; i < subAny.length; ++i) {
                subAnyArray[i] = new ASN1OctetString(subAny[i]);
            }
        }
        ASN1OctetString subFinalOS;
        if (subFinal == null) {
            subFinalOS = null;
        }
        else {
            subFinalOS = new ASN1OctetString(subFinal);
        }
        return new Filter(null, (byte)(-92), Filter.NO_FILTERS, null, attributeName, null, subInitialOS, subAnyArray, subFinalOS, null, false);
    }
    
    static Filter createSubstringFilter(final String attributeName, final ASN1OctetString subInitial, final ASN1OctetString[] subAny, final ASN1OctetString subFinal) {
        Validator.ensureNotNull(attributeName);
        Validator.ensureTrue(subInitial != null || (subAny != null && subAny.length > 0) || subFinal != null);
        if (subAny == null) {
            return new Filter(null, (byte)(-92), Filter.NO_FILTERS, null, attributeName, null, subInitial, Filter.NO_SUB_ANY, subFinal, null, false);
        }
        return new Filter(null, (byte)(-92), Filter.NO_FILTERS, null, attributeName, null, subInitial, subAny, subFinal, null, false);
    }
    
    public static Filter createSubInitialFilter(final String attributeName, final String subInitial) {
        return createSubstringFilter(attributeName, subInitial, null, null);
    }
    
    public static Filter createSubInitialFilter(final String attributeName, final byte[] subInitial) {
        return createSubstringFilter(attributeName, subInitial, null, null);
    }
    
    public static Filter createSubAnyFilter(final String attributeName, final String... subAny) {
        return createSubstringFilter(attributeName, null, subAny, null);
    }
    
    public static Filter createSubAnyFilter(final String attributeName, final byte[]... subAny) {
        return createSubstringFilter(attributeName, null, subAny, null);
    }
    
    public static Filter createSubFinalFilter(final String attributeName, final String subFinal) {
        return createSubstringFilter(attributeName, null, null, subFinal);
    }
    
    public static Filter createSubFinalFilter(final String attributeName, final byte[] subFinal) {
        return createSubstringFilter(attributeName, null, null, subFinal);
    }
    
    public static Filter createGreaterOrEqualFilter(final String attributeName, final String assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-91), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createGreaterOrEqualFilter(final String attributeName, final byte[] assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-91), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    static Filter createGreaterOrEqualFilter(final String attributeName, final ASN1OctetString assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-91), Filter.NO_FILTERS, null, attributeName, assertionValue, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createLessOrEqualFilter(final String attributeName, final String assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-90), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createLessOrEqualFilter(final String attributeName, final byte[] assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-90), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    static Filter createLessOrEqualFilter(final String attributeName, final ASN1OctetString assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-90), Filter.NO_FILTERS, null, attributeName, assertionValue, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createPresenceFilter(final String attributeName) {
        Validator.ensureNotNull(attributeName);
        return new Filter(null, (byte)(-121), Filter.NO_FILTERS, null, attributeName, null, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createApproximateMatchFilter(final String attributeName, final String assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-88), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createApproximateMatchFilter(final String attributeName, final byte[] assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-88), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    static Filter createApproximateMatchFilter(final String attributeName, final ASN1OctetString assertionValue) {
        Validator.ensureNotNull(attributeName, assertionValue);
        return new Filter(null, (byte)(-88), Filter.NO_FILTERS, null, attributeName, assertionValue, null, Filter.NO_SUB_ANY, null, null, false);
    }
    
    public static Filter createExtensibleMatchFilter(final String attributeName, final String matchingRuleID, final boolean dnAttributes, final String assertionValue) {
        Validator.ensureNotNull(assertionValue);
        Validator.ensureFalse(attributeName == null && matchingRuleID == null);
        return new Filter(null, (byte)(-87), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, matchingRuleID, dnAttributes);
    }
    
    public static Filter createExtensibleMatchFilter(final String attributeName, final String matchingRuleID, final boolean dnAttributes, final byte[] assertionValue) {
        Validator.ensureNotNull(assertionValue);
        Validator.ensureFalse(attributeName == null && matchingRuleID == null);
        return new Filter(null, (byte)(-87), Filter.NO_FILTERS, null, attributeName, new ASN1OctetString(assertionValue), null, Filter.NO_SUB_ANY, null, matchingRuleID, dnAttributes);
    }
    
    static Filter createExtensibleMatchFilter(final String attributeName, final String matchingRuleID, final boolean dnAttributes, final ASN1OctetString assertionValue) {
        Validator.ensureNotNull(assertionValue);
        Validator.ensureFalse(attributeName == null && matchingRuleID == null);
        return new Filter(null, (byte)(-87), Filter.NO_FILTERS, null, attributeName, assertionValue, null, Filter.NO_SUB_ANY, null, matchingRuleID, dnAttributes);
    }
    
    public static Filter create(final String filterString) throws LDAPException {
        Validator.ensureNotNull(filterString);
        return create(filterString, 0, filterString.length() - 1, 0);
    }
    
    private static Filter create(final String filterString, final int startPos, final int endPos, final int depth) throws LDAPException {
        if (depth > 100) {
            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_TOO_DEEP.get(filterString));
        }
        if (startPos >= endPos) {
            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_TOO_SHORT.get(filterString));
        }
        int l = startPos;
        int r = endPos;
        if (filterString.charAt(l) == '(') {
            if (filterString.charAt(r) != ')') {
                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_OPEN_WITHOUT_CLOSE.get(filterString, l, r));
            }
            ++l;
            --r;
        }
        else if (l != 0) {
            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_MISSING_PARENTHESES.get(filterString, filterString.substring(l, r + 1)));
        }
        byte filterType = 0;
        Filter[] filterComps = null;
        Filter notComp = null;
        String attrName = null;
        ASN1OctetString assertionValue = null;
        ASN1OctetString subInitial = null;
        ASN1OctetString[] subAny = null;
        ASN1OctetString subFinal = null;
        String matchingRuleID = null;
        boolean dnAttributes = false;
        switch (filterString.charAt(l)) {
            case '&': {
                filterType = -96;
                filterComps = parseFilterComps(filterString, l + 1, r, depth + 1);
                notComp = null;
                attrName = null;
                assertionValue = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                matchingRuleID = null;
                dnAttributes = false;
                break;
            }
            case '|': {
                filterType = -95;
                filterComps = parseFilterComps(filterString, l + 1, r, depth + 1);
                notComp = null;
                attrName = null;
                assertionValue = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                matchingRuleID = null;
                dnAttributes = false;
                break;
            }
            case '!': {
                filterType = -94;
                filterComps = Filter.NO_FILTERS;
                notComp = create(filterString, l + 1, r, depth + 1);
                attrName = null;
                assertionValue = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                matchingRuleID = null;
                dnAttributes = false;
                break;
            }
            case '(': {
                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_OPEN_PAREN.get(filterString, l));
            }
            case ':': {
                filterType = -87;
                filterComps = Filter.NO_FILTERS;
                notComp = null;
                attrName = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                int dnMRIDStart;
                for (dnMRIDStart = ++l; l <= r && filterString.charAt(l) != ':'; ++l) {}
                if (l > r) {
                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_NO_COLON_AFTER_MRID.get(filterString, startPos));
                }
                if (l == dnMRIDStart) {
                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_EMPTY_MRID.get(filterString, startPos));
                }
                final String s = filterString.substring(dnMRIDStart, l++);
                if (s.equalsIgnoreCase("dn")) {
                    dnAttributes = true;
                    final int mrIDStart = l;
                    while (l < r && filterString.charAt(l) != ':') {
                        ++l;
                    }
                    if (l >= r) {
                        throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_NO_COLON_AFTER_MRID.get(filterString, startPos));
                    }
                    matchingRuleID = filterString.substring(mrIDStart, l);
                    if (matchingRuleID.isEmpty()) {
                        throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_EMPTY_MRID.get(filterString, startPos));
                    }
                    if (++l > r || filterString.charAt(l) != '=') {
                        throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_CHAR_AFTER_MRID.get(filterString, startPos, filterString.charAt(l)));
                    }
                }
                else {
                    matchingRuleID = s;
                    dnAttributes = false;
                    if (l > r || filterString.charAt(l) != '=') {
                        throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_NO_EQUAL_AFTER_MRID.get(filterString, startPos));
                    }
                }
                ++l;
                final ByteStringBuffer valueBuffer = new ByteStringBuffer(r - l + 1);
                while (l <= r) {
                    final char c = filterString.charAt(l);
                    if (c == '\\') {
                        l = readEscapedHexString(filterString, ++l, valueBuffer);
                    }
                    else {
                        if (c == '(') {
                            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_OPEN_PAREN.get(filterString, l));
                        }
                        if (c == ')') {
                            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_CLOSE_PAREN.get(filterString, l));
                        }
                        valueBuffer.append(c);
                        ++l;
                    }
                }
                assertionValue = new ASN1OctetString(valueBuffer.toByteArray());
                break;
            }
            default: {
                filterComps = Filter.NO_FILTERS;
                notComp = null;
                final int attrStartPos = l;
                int attrEndPos = -1;
                byte tempFilterType = 0;
                boolean filterTypeKnown = false;
                boolean equalFound = false;
            Label_1451:
                while (l <= r) {
                    final char c2 = filterString.charAt(l++);
                    switch (c2) {
                        case ':': {
                            tempFilterType = -87;
                            filterTypeKnown = true;
                            attrEndPos = l - 1;
                            break Label_1451;
                        }
                        case '>': {
                            tempFilterType = -91;
                            filterTypeKnown = true;
                            attrEndPos = l - 1;
                            if (l > r) {
                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_END_AFTER_GT.get(filterString, startPos));
                            }
                            if (filterString.charAt(l++) != '=') {
                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_CHAR_AFTER_GT.get(filterString, startPos, filterString.charAt(l - 1)));
                            }
                            break Label_1451;
                        }
                        case '<': {
                            tempFilterType = -90;
                            filterTypeKnown = true;
                            attrEndPos = l - 1;
                            if (l > r) {
                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_END_AFTER_LT.get(filterString, startPos));
                            }
                            if (filterString.charAt(l++) != '=') {
                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_CHAR_AFTER_LT.get(filterString, startPos, filterString.charAt(l - 1)));
                            }
                            break Label_1451;
                        }
                        case '~': {
                            tempFilterType = -88;
                            filterTypeKnown = true;
                            attrEndPos = l - 1;
                            if (l > r) {
                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_END_AFTER_TILDE.get(filterString, startPos));
                            }
                            if (filterString.charAt(l++) != '=') {
                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_CHAR_AFTER_TILDE.get(filterString, startPos, filterString.charAt(l - 1)));
                            }
                            break Label_1451;
                        }
                        case '=': {
                            attrEndPos = l - 1;
                            equalFound = true;
                            break Label_1451;
                        }
                        default: {
                            continue;
                        }
                    }
                }
                if (attrEndPos > attrStartPos) {
                    attrName = filterString.substring(attrStartPos, attrEndPos);
                    if (filterTypeKnown && tempFilterType == -87) {
                        if (l > r) {
                            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_NO_EQUAL_SIGN.get(filterString, startPos));
                        }
                        final char c2 = filterString.charAt(l++);
                        if (c2 == '=') {
                            matchingRuleID = null;
                            dnAttributes = false;
                        }
                        else {
                            equalFound = false;
                            final int substrStartPos = l - 1;
                            while (l <= r) {
                                if (filterString.charAt(l++) == '=') {
                                    equalFound = true;
                                    break;
                                }
                            }
                            if (!equalFound) {
                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_NO_EQUAL_SIGN.get(filterString, startPos));
                            }
                            final String substr = filterString.substring(substrStartPos, l - 1);
                            final String lowerSubstr = StaticUtils.toLowerCase(substr);
                            if (!substr.endsWith(":")) {
                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_CANNOT_PARSE_MRID.get(filterString, startPos));
                            }
                            if (lowerSubstr.equals("dn:")) {
                                matchingRuleID = null;
                                dnAttributes = true;
                            }
                            else if (lowerSubstr.startsWith("dn:")) {
                                matchingRuleID = substr.substring(3, substr.length() - 1);
                                if (matchingRuleID.isEmpty()) {
                                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_EMPTY_MRID.get(filterString, startPos));
                                }
                                dnAttributes = true;
                            }
                            else {
                                matchingRuleID = substr.substring(0, substr.length() - 1);
                                dnAttributes = false;
                                if (matchingRuleID.isEmpty()) {
                                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_EMPTY_MRID.get(filterString, startPos));
                                }
                            }
                        }
                    }
                    else {
                        matchingRuleID = null;
                        dnAttributes = false;
                    }
                    if (l > r) {
                        assertionValue = new ASN1OctetString();
                        if (!filterTypeKnown) {
                            tempFilterType = -93;
                        }
                        subInitial = null;
                        subAny = Filter.NO_SUB_ANY;
                        subFinal = null;
                    }
                    else if (l == r) {
                        if (filterTypeKnown) {
                            switch (filterString.charAt(l)) {
                                case '(':
                                case ')':
                                case '*':
                                case '\\': {
                                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_CHAR_IN_AV.get(filterString, startPos, filterString.charAt(l)));
                                }
                                default: {
                                    assertionValue = new ASN1OctetString(filterString.substring(l, l + 1));
                                    break;
                                }
                            }
                        }
                        else {
                            final char c2 = filterString.charAt(l);
                            switch (c2) {
                                case '*': {
                                    tempFilterType = -121;
                                    assertionValue = null;
                                    break;
                                }
                                case '(':
                                case ')':
                                case '\\': {
                                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_CHAR_IN_AV.get(filterString, startPos, filterString.charAt(l)));
                                }
                                default: {
                                    tempFilterType = -93;
                                    assertionValue = new ASN1OctetString(filterString.substring(l, l + 1));
                                    break;
                                }
                            }
                        }
                        subInitial = null;
                        subAny = Filter.NO_SUB_ANY;
                        subFinal = null;
                    }
                    else {
                        if (!filterTypeKnown) {
                            tempFilterType = -93;
                        }
                        final int valueStartPos = l;
                        ASN1OctetString tempSubInitial = null;
                        ASN1OctetString tempSubFinal = null;
                        final ArrayList<ASN1OctetString> subAnyList = new ArrayList<ASN1OctetString>(1);
                        ByteStringBuffer buffer = new ByteStringBuffer(r - l + 1);
                        while (l <= r) {
                            final char c3 = filterString.charAt(l++);
                            switch (c3) {
                                case '*': {
                                    if (filterTypeKnown) {
                                        throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_ASTERISK.get(filterString, startPos));
                                    }
                                    if (l - 1 != valueStartPos) {
                                        if (tempFilterType == -92) {
                                            if (buffer.length() == 0) {
                                                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_DOUBLE_ASTERISK.get(filterString, startPos));
                                            }
                                            subAnyList.add(new ASN1OctetString(buffer.toByteArray()));
                                            buffer = new ByteStringBuffer(r - l + 1);
                                        }
                                        else {
                                            tempSubInitial = new ASN1OctetString(buffer.toByteArray());
                                            buffer = new ByteStringBuffer(r - l + 1);
                                        }
                                    }
                                    tempFilterType = -92;
                                    continue;
                                }
                                case '\\': {
                                    l = readEscapedHexString(filterString, l, buffer);
                                    continue;
                                }
                                case '(': {
                                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_OPEN_PAREN.get(filterString, l));
                                }
                                case ')': {
                                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_UNEXPECTED_CLOSE_PAREN.get(filterString, l));
                                }
                                default: {
                                    if (Character.isHighSurrogate(c3) && l <= r) {
                                        final char c4 = filterString.charAt(l);
                                        if (Character.isLowSurrogate(c4)) {
                                            ++l;
                                            final int codePoint = Character.toCodePoint(c3, c4);
                                            buffer.append((CharSequence)new String(new int[] { codePoint }, 0, 1));
                                            continue;
                                        }
                                    }
                                    buffer.append(c3);
                                    continue;
                                }
                            }
                        }
                        if (tempFilterType == -92 && !buffer.isEmpty()) {
                            tempSubFinal = new ASN1OctetString(buffer.toByteArray());
                        }
                        subInitial = tempSubInitial;
                        subAny = subAnyList.toArray(new ASN1OctetString[subAnyList.size()]);
                        subFinal = tempSubFinal;
                        if (tempFilterType == -92) {
                            assertionValue = null;
                        }
                        else {
                            assertionValue = new ASN1OctetString(buffer.toByteArray());
                        }
                    }
                    filterType = tempFilterType;
                    break;
                }
                if (equalFound) {
                    throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_EMPTY_ATTR_NAME.get(filterString, startPos));
                }
                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_NO_EQUAL_SIGN.get(filterString, startPos));
            }
        }
        if (startPos == 0) {
            return new Filter(filterString, filterType, filterComps, notComp, attrName, assertionValue, subInitial, subAny, subFinal, matchingRuleID, dnAttributes);
        }
        return new Filter(filterString.substring(startPos, endPos + 1), filterType, filterComps, notComp, attrName, assertionValue, subInitial, subAny, subFinal, matchingRuleID, dnAttributes);
    }
    
    private static Filter[] parseFilterComps(final String filterString, final int startPos, final int endPos, final int depth) throws LDAPException {
        if (startPos > endPos) {
            return Filter.NO_FILTERS;
        }
        if (filterString.charAt(startPos) != '(') {
            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_EXPECTED_OPEN_PAREN.get(filterString, startPos));
        }
        if (filterString.charAt(endPos) != ')') {
            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_EXPECTED_CLOSE_PAREN.get(filterString, startPos));
        }
        final ArrayList<Filter> filterList = new ArrayList<Filter>(5);
        int filterStartPos = startPos;
        int pos = startPos;
        int numOpen = 0;
        while (pos <= endPos) {
            final char c = filterString.charAt(pos++);
            if (c == '(') {
                ++numOpen;
            }
            else {
                if (c != ')' || --numOpen != 0) {
                    continue;
                }
                filterList.add(create(filterString, filterStartPos, pos - 1, depth));
                filterStartPos = pos;
            }
        }
        if (numOpen != 0) {
            throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_MISMATCHED_PARENS.get(filterString, startPos, endPos));
        }
        return filterList.toArray(new Filter[filterList.size()]);
    }
    
    private static int readEscapedHexString(final String filterString, final int startPos, final ByteStringBuffer buffer) throws LDAPException {
        byte b = 0;
        switch (filterString.charAt(startPos)) {
            case '0': {
                b = 0;
                break;
            }
            case '1': {
                b = 16;
                break;
            }
            case '2': {
                b = 32;
                break;
            }
            case '3': {
                b = 48;
                break;
            }
            case '4': {
                b = 64;
                break;
            }
            case '5': {
                b = 80;
                break;
            }
            case '6': {
                b = 96;
                break;
            }
            case '7': {
                b = 112;
                break;
            }
            case '8': {
                b = -128;
                break;
            }
            case '9': {
                b = -112;
                break;
            }
            case 'A':
            case 'a': {
                b = -96;
                break;
            }
            case 'B':
            case 'b': {
                b = -80;
                break;
            }
            case 'C':
            case 'c': {
                b = -64;
                break;
            }
            case 'D':
            case 'd': {
                b = -48;
                break;
            }
            case 'E':
            case 'e': {
                b = -32;
                break;
            }
            case 'F':
            case 'f': {
                b = -16;
                break;
            }
            default: {
                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_INVALID_HEX_CHAR.get(filterString, filterString.charAt(startPos), startPos));
            }
        }
        switch (filterString.charAt(startPos + 1)) {
            case '0': {
                buffer.append(b);
                break;
            }
            case '1': {
                buffer.append((byte)(b | 0x1));
                break;
            }
            case '2': {
                buffer.append((byte)(b | 0x2));
                break;
            }
            case '3': {
                buffer.append((byte)(b | 0x3));
                break;
            }
            case '4': {
                buffer.append((byte)(b | 0x4));
                break;
            }
            case '5': {
                buffer.append((byte)(b | 0x5));
                break;
            }
            case '6': {
                buffer.append((byte)(b | 0x6));
                break;
            }
            case '7': {
                buffer.append((byte)(b | 0x7));
                break;
            }
            case '8': {
                buffer.append((byte)(b | 0x8));
                break;
            }
            case '9': {
                buffer.append((byte)(b | 0x9));
                break;
            }
            case 'A':
            case 'a': {
                buffer.append((byte)(b | 0xA));
                break;
            }
            case 'B':
            case 'b': {
                buffer.append((byte)(b | 0xB));
                break;
            }
            case 'C':
            case 'c': {
                buffer.append((byte)(b | 0xC));
                break;
            }
            case 'D':
            case 'd': {
                buffer.append((byte)(b | 0xD));
                break;
            }
            case 'E':
            case 'e': {
                buffer.append((byte)(b | 0xE));
                break;
            }
            case 'F':
            case 'f': {
                buffer.append((byte)(b | 0xF));
                break;
            }
            default: {
                throw new LDAPException(ResultCode.FILTER_ERROR, LDAPMessages.ERR_FILTER_INVALID_HEX_CHAR.get(filterString, filterString.charAt(startPos + 1), startPos + 1));
            }
        }
        return startPos + 2;
    }
    
    public void writeTo(final ASN1Buffer buffer) {
        switch (this.filterType) {
            case -96:
            case -95: {
                final ASN1BufferSet compSet = buffer.beginSet(this.filterType);
                for (final Filter f : this.filterComps) {
                    f.writeTo(buffer);
                }
                compSet.end();
                break;
            }
            case -94: {
                buffer.addElement(new ASN1Element(this.filterType, this.notComp.encode().encode()));
                break;
            }
            case -93:
            case -91:
            case -90:
            case -88: {
                final ASN1BufferSequence avaSequence = buffer.beginSequence(this.filterType);
                buffer.addOctetString(this.attrName);
                buffer.addElement(this.assertionValue);
                avaSequence.end();
                break;
            }
            case -92: {
                final ASN1BufferSequence subFilterSequence = buffer.beginSequence(this.filterType);
                buffer.addOctetString(this.attrName);
                final ASN1BufferSequence valueSequence = buffer.beginSequence();
                if (this.subInitial != null) {
                    buffer.addOctetString((byte)(-128), this.subInitial.getValue());
                }
                for (final ASN1OctetString s : this.subAny) {
                    buffer.addOctetString((byte)(-127), s.getValue());
                }
                if (this.subFinal != null) {
                    buffer.addOctetString((byte)(-126), this.subFinal.getValue());
                }
                valueSequence.end();
                subFilterSequence.end();
                break;
            }
            case -121: {
                buffer.addOctetString(this.filterType, this.attrName);
                break;
            }
            case -87: {
                final ASN1BufferSequence mrSequence = buffer.beginSequence(this.filterType);
                if (this.matchingRuleID != null) {
                    buffer.addOctetString((byte)(-127), this.matchingRuleID);
                }
                if (this.attrName != null) {
                    buffer.addOctetString((byte)(-126), this.attrName);
                }
                buffer.addOctetString((byte)(-125), this.assertionValue.getValue());
                if (this.dnAttributes) {
                    buffer.addBoolean((byte)(-124), true);
                }
                mrSequence.end();
                break;
            }
        }
    }
    
    public ASN1Element encode() {
        switch (this.filterType) {
            case -96:
            case -95: {
                final ASN1Element[] filterElements = new ASN1Element[this.filterComps.length];
                for (int i = 0; i < this.filterComps.length; ++i) {
                    filterElements[i] = this.filterComps[i].encode();
                }
                return new ASN1Set(this.filterType, filterElements);
            }
            case -94: {
                return new ASN1Element(this.filterType, this.notComp.encode().encode());
            }
            case -93:
            case -91:
            case -90:
            case -88: {
                final ASN1OctetString[] attrValueAssertionElements = { new ASN1OctetString(this.attrName), this.assertionValue };
                return new ASN1Sequence(this.filterType, (ASN1Element[])attrValueAssertionElements);
            }
            case -92: {
                final ArrayList<ASN1OctetString> subList = new ArrayList<ASN1OctetString>(2 + this.subAny.length);
                if (this.subInitial != null) {
                    subList.add(new ASN1OctetString((byte)(-128), this.subInitial.getValue()));
                }
                for (final ASN1Element subAnyElement : this.subAny) {
                    subList.add(new ASN1OctetString((byte)(-127), subAnyElement.getValue()));
                }
                if (this.subFinal != null) {
                    subList.add(new ASN1OctetString((byte)(-126), this.subFinal.getValue()));
                }
                final ASN1Element[] subFilterElements = { new ASN1OctetString(this.attrName), new ASN1Sequence(subList) };
                return new ASN1Sequence(this.filterType, subFilterElements);
            }
            case -121: {
                return new ASN1OctetString(this.filterType, this.attrName);
            }
            case -87: {
                final ArrayList<ASN1Element> emElementList = new ArrayList<ASN1Element>(4);
                if (this.matchingRuleID != null) {
                    emElementList.add(new ASN1OctetString((byte)(-127), this.matchingRuleID));
                }
                if (this.attrName != null) {
                    emElementList.add(new ASN1OctetString((byte)(-126), this.attrName));
                }
                emElementList.add(new ASN1OctetString((byte)(-125), this.assertionValue.getValue()));
                if (this.dnAttributes) {
                    emElementList.add(new ASN1Boolean((byte)(-124), true));
                }
                return new ASN1Sequence(this.filterType, emElementList);
            }
            default: {
                throw new AssertionError((Object)LDAPMessages.ERR_FILTER_INVALID_TYPE.get(StaticUtils.toHex(this.filterType)));
            }
        }
    }
    
    public static Filter readFrom(final ASN1StreamReader reader) throws LDAPException {
        try {
            final byte filterType = (byte)reader.peek();
            Filter[] filterComps = null;
            Filter notComp = null;
            String attrName = null;
            ASN1OctetString assertionValue = null;
            ASN1OctetString subInitial = null;
            ASN1OctetString[] subAny = null;
            ASN1OctetString subFinal = null;
            String matchingRuleID = null;
            boolean dnAttributes = false;
            switch (filterType) {
                case -96:
                case -95: {
                    final ArrayList<Filter> comps = new ArrayList<Filter>(5);
                    final ASN1StreamReaderSet elementSet = reader.beginSet();
                    while (elementSet.hasMoreElements()) {
                        comps.add(readFrom(reader));
                    }
                    filterComps = new Filter[comps.size()];
                    comps.toArray(filterComps);
                    notComp = null;
                    attrName = null;
                    assertionValue = null;
                    subInitial = null;
                    subAny = Filter.NO_SUB_ANY;
                    subFinal = null;
                    matchingRuleID = null;
                    dnAttributes = false;
                    break;
                }
                case -94: {
                    ASN1Element notFilterElement;
                    try {
                        final ASN1Element e = reader.readElement();
                        notFilterElement = ASN1Element.decode(e.getValue());
                    }
                    catch (final ASN1Exception ae) {
                        Debug.debugException(ae);
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_CANNOT_DECODE_NOT_COMP.get(StaticUtils.getExceptionMessage(ae)), ae);
                    }
                    notComp = decode(notFilterElement);
                    filterComps = Filter.NO_FILTERS;
                    attrName = null;
                    assertionValue = null;
                    subInitial = null;
                    subAny = Filter.NO_SUB_ANY;
                    subFinal = null;
                    matchingRuleID = null;
                    dnAttributes = false;
                    break;
                }
                case -93:
                case -91:
                case -90:
                case -88: {
                    reader.beginSequence();
                    attrName = reader.readString();
                    assertionValue = new ASN1OctetString(reader.readBytes());
                    filterComps = Filter.NO_FILTERS;
                    notComp = null;
                    subInitial = null;
                    subAny = Filter.NO_SUB_ANY;
                    subFinal = null;
                    matchingRuleID = null;
                    dnAttributes = false;
                    break;
                }
                case -92: {
                    reader.beginSequence();
                    attrName = reader.readString();
                    ASN1OctetString tempSubInitial = null;
                    ASN1OctetString tempSubFinal = null;
                    final ArrayList<ASN1OctetString> subAnyList = new ArrayList<ASN1OctetString>(1);
                    final ASN1StreamReaderSequence subSequence = reader.beginSequence();
                    while (subSequence.hasMoreElements()) {
                        final byte type = (byte)reader.peek();
                        final ASN1OctetString s = new ASN1OctetString(type, reader.readBytes());
                        switch (type) {
                            case Byte.MIN_VALUE: {
                                tempSubInitial = s;
                                continue;
                            }
                            case -127: {
                                subAnyList.add(s);
                                continue;
                            }
                            case -126: {
                                tempSubFinal = s;
                                continue;
                            }
                            default: {
                                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_INVALID_SUBSTR_TYPE.get(StaticUtils.toHex(type)));
                            }
                        }
                    }
                    subInitial = tempSubInitial;
                    subFinal = tempSubFinal;
                    subAny = new ASN1OctetString[subAnyList.size()];
                    subAnyList.toArray(subAny);
                    filterComps = Filter.NO_FILTERS;
                    notComp = null;
                    assertionValue = null;
                    matchingRuleID = null;
                    dnAttributes = false;
                    break;
                }
                case -121: {
                    attrName = reader.readString();
                    filterComps = Filter.NO_FILTERS;
                    notComp = null;
                    assertionValue = null;
                    subInitial = null;
                    subAny = Filter.NO_SUB_ANY;
                    subFinal = null;
                    matchingRuleID = null;
                    dnAttributes = false;
                    break;
                }
                case -87: {
                    String tempAttrName = null;
                    ASN1OctetString tempAssertionValue = null;
                    String tempMatchingRuleID = null;
                    boolean tempDNAttributes = false;
                    final ASN1StreamReaderSequence emSequence = reader.beginSequence();
                    while (emSequence.hasMoreElements()) {
                        final byte type2 = (byte)reader.peek();
                        switch (type2) {
                            case -126: {
                                tempAttrName = reader.readString();
                                continue;
                            }
                            case -127: {
                                tempMatchingRuleID = reader.readString();
                                continue;
                            }
                            case -125: {
                                tempAssertionValue = new ASN1OctetString(type2, reader.readBytes());
                                continue;
                            }
                            case -124: {
                                tempDNAttributes = reader.readBoolean();
                                continue;
                            }
                            default: {
                                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_INVALID_TYPE.get(StaticUtils.toHex(type2)));
                            }
                        }
                    }
                    if (tempAttrName == null && tempMatchingRuleID == null) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_NO_ATTR_OR_MRID.get());
                    }
                    if (tempAssertionValue == null) {
                        throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_NO_VALUE.get());
                    }
                    attrName = tempAttrName;
                    assertionValue = tempAssertionValue;
                    matchingRuleID = tempMatchingRuleID;
                    dnAttributes = tempDNAttributes;
                    filterComps = Filter.NO_FILTERS;
                    notComp = null;
                    subInitial = null;
                    subAny = Filter.NO_SUB_ANY;
                    subFinal = null;
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_ELEMENT_INVALID_TYPE.get(StaticUtils.toHex(filterType)));
                }
            }
            return new Filter(null, filterType, filterComps, notComp, attrName, assertionValue, subInitial, subAny, subFinal, matchingRuleID, dnAttributes);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_CANNOT_DECODE.get(StaticUtils.getExceptionMessage(e2)), e2);
        }
    }
    
    public static Filter decode(final ASN1Element filterElement) throws LDAPException {
        final byte filterType = filterElement.getType();
        Filter notComp = null;
        String attrName = null;
        ASN1OctetString assertionValue = null;
        ASN1OctetString subInitial = null;
        ASN1OctetString[] subAny = null;
        ASN1OctetString subFinal = null;
        String matchingRuleID = null;
        boolean dnAttributes = false;
        Filter[] filterComps = null;
        switch (filterType) {
            case -96:
            case -95: {
                notComp = null;
                attrName = null;
                assertionValue = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                matchingRuleID = null;
                dnAttributes = false;
                ASN1Set compSet;
                try {
                    compSet = ASN1Set.decodeAsSet(filterElement);
                }
                catch (final ASN1Exception ae) {
                    Debug.debugException(ae);
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_CANNOT_DECODE_COMPS.get(StaticUtils.getExceptionMessage(ae)), ae);
                }
                final ASN1Element[] compElements = compSet.elements();
                filterComps = new Filter[compElements.length];
                for (int i = 0; i < compElements.length; ++i) {
                    filterComps[i] = decode(compElements[i]);
                }
                break;
            }
            case -94: {
                filterComps = Filter.NO_FILTERS;
                attrName = null;
                assertionValue = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                matchingRuleID = null;
                dnAttributes = false;
                ASN1Element notFilterElement;
                try {
                    notFilterElement = ASN1Element.decode(filterElement.getValue());
                }
                catch (final ASN1Exception ae2) {
                    Debug.debugException(ae2);
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_CANNOT_DECODE_NOT_COMP.get(StaticUtils.getExceptionMessage(ae2)), ae2);
                }
                notComp = decode(notFilterElement);
                break;
            }
            case -93:
            case -91:
            case -90:
            case -88: {
                filterComps = Filter.NO_FILTERS;
                notComp = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                matchingRuleID = null;
                dnAttributes = false;
                ASN1Sequence avaSequence;
                try {
                    avaSequence = ASN1Sequence.decodeAsSequence(filterElement);
                }
                catch (final ASN1Exception ae3) {
                    Debug.debugException(ae3);
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_CANNOT_DECODE_AVA.get(StaticUtils.getExceptionMessage(ae3)), ae3);
                }
                final ASN1Element[] avaElements = avaSequence.elements();
                if (avaElements.length != 2) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_INVALID_AVA_ELEMENT_COUNT.get(avaElements.length));
                }
                attrName = ASN1OctetString.decodeAsOctetString(avaElements[0]).stringValue();
                assertionValue = ASN1OctetString.decodeAsOctetString(avaElements[1]);
                break;
            }
            case -92: {
                filterComps = Filter.NO_FILTERS;
                notComp = null;
                assertionValue = null;
                matchingRuleID = null;
                dnAttributes = false;
                ASN1Sequence subFilterSequence;
                try {
                    subFilterSequence = ASN1Sequence.decodeAsSequence(filterElement);
                }
                catch (final ASN1Exception ae4) {
                    Debug.debugException(ae4);
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_CANNOT_DECODE_SUBSTRING.get(StaticUtils.getExceptionMessage(ae4)), ae4);
                }
                final ASN1Element[] subFilterElements = subFilterSequence.elements();
                if (subFilterElements.length != 2) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_INVALID_SUBSTR_ASSERTION_COUNT.get(subFilterElements.length));
                }
                attrName = ASN1OctetString.decodeAsOctetString(subFilterElements[0]).stringValue();
                ASN1Sequence subSequence;
                try {
                    subSequence = ASN1Sequence.decodeAsSequence(subFilterElements[1]);
                }
                catch (final ASN1Exception ae5) {
                    Debug.debugException(ae5);
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_CANNOT_DECODE_SUBSTRING.get(StaticUtils.getExceptionMessage(ae5)), ae5);
                }
                ASN1OctetString tempSubInitial = null;
                ASN1OctetString tempSubFinal = null;
                final ArrayList<ASN1OctetString> subAnyList = new ArrayList<ASN1OctetString>(1);
                final ASN1Element[] arr$;
                final ASN1Element[] subElements = arr$ = subSequence.elements();
                for (final ASN1Element subElement : arr$) {
                    switch (subElement.getType()) {
                        case Byte.MIN_VALUE: {
                            if (tempSubInitial == null) {
                                tempSubInitial = ASN1OctetString.decodeAsOctetString(subElement);
                                break;
                            }
                            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_MULTIPLE_SUBINITIAL.get());
                        }
                        case -127: {
                            subAnyList.add(ASN1OctetString.decodeAsOctetString(subElement));
                            break;
                        }
                        case -126: {
                            if (tempSubFinal == null) {
                                tempSubFinal = ASN1OctetString.decodeAsOctetString(subElement);
                                break;
                            }
                            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_MULTIPLE_SUBFINAL.get());
                        }
                        default: {
                            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_INVALID_SUBSTR_TYPE.get(StaticUtils.toHex(subElement.getType())));
                        }
                    }
                }
                subInitial = tempSubInitial;
                subAny = subAnyList.toArray(new ASN1OctetString[subAnyList.size()]);
                subFinal = tempSubFinal;
                break;
            }
            case -121: {
                filterComps = Filter.NO_FILTERS;
                notComp = null;
                assertionValue = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                matchingRuleID = null;
                dnAttributes = false;
                attrName = ASN1OctetString.decodeAsOctetString(filterElement).stringValue();
                break;
            }
            case -87: {
                filterComps = Filter.NO_FILTERS;
                notComp = null;
                subInitial = null;
                subAny = Filter.NO_SUB_ANY;
                subFinal = null;
                ASN1Sequence emSequence;
                try {
                    emSequence = ASN1Sequence.decodeAsSequence(filterElement);
                }
                catch (final ASN1Exception ae6) {
                    Debug.debugException(ae6);
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_CANNOT_DECODE_EXTMATCH.get(StaticUtils.getExceptionMessage(ae6)), ae6);
                }
                String tempAttrName = null;
                ASN1OctetString tempAssertionValue = null;
                String tempMatchingRuleID = null;
                boolean tempDNAttributes = false;
                for (final ASN1Element e : emSequence.elements()) {
                    switch (e.getType()) {
                        case -126: {
                            if (tempAttrName == null) {
                                tempAttrName = ASN1OctetString.decodeAsOctetString(e).stringValue();
                                break;
                            }
                            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_MULTIPLE_ATTRS.get());
                        }
                        case -127: {
                            if (tempMatchingRuleID == null) {
                                tempMatchingRuleID = ASN1OctetString.decodeAsOctetString(e).stringValue();
                                break;
                            }
                            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_MULTIPLE_MRIDS.get());
                        }
                        case -125: {
                            if (tempAssertionValue == null) {
                                tempAssertionValue = ASN1OctetString.decodeAsOctetString(e);
                                break;
                            }
                            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_MULTIPLE_VALUES.get());
                        }
                        case -124: {
                            try {
                                if (tempDNAttributes) {
                                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_MULTIPLE_DNATTRS.get());
                                }
                                tempDNAttributes = ASN1Boolean.decodeAsBoolean(e).booleanValue();
                                break;
                            }
                            catch (final ASN1Exception ae7) {
                                Debug.debugException(ae7);
                                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_DNATTRS_NOT_BOOLEAN.get(StaticUtils.getExceptionMessage(ae7)), ae7);
                            }
                            throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_INVALID_TYPE.get(StaticUtils.toHex(e.getType())));
                        }
                    }
                }
                if (tempAttrName == null && tempMatchingRuleID == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_NO_ATTR_OR_MRID.get());
                }
                if (tempAssertionValue == null) {
                    throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_EXTMATCH_NO_VALUE.get());
                }
                attrName = tempAttrName;
                assertionValue = tempAssertionValue;
                matchingRuleID = tempMatchingRuleID;
                dnAttributes = tempDNAttributes;
                break;
            }
            default: {
                throw new LDAPException(ResultCode.DECODING_ERROR, LDAPMessages.ERR_FILTER_ELEMENT_INVALID_TYPE.get(StaticUtils.toHex(filterElement.getType())));
            }
        }
        return new Filter(null, filterType, filterComps, notComp, attrName, assertionValue, subInitial, subAny, subFinal, matchingRuleID, dnAttributes);
    }
    
    public byte getFilterType() {
        return this.filterType;
    }
    
    public Filter[] getComponents() {
        return this.filterComps;
    }
    
    public Filter getNOTComponent() {
        return this.notComp;
    }
    
    public String getAttributeName() {
        return this.attrName;
    }
    
    public String getAssertionValue() {
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
    
    public ASN1OctetString getRawAssertionValue() {
        return this.assertionValue;
    }
    
    public String getSubInitialString() {
        if (this.subInitial == null) {
            return null;
        }
        return this.subInitial.stringValue();
    }
    
    public byte[] getSubInitialBytes() {
        if (this.subInitial == null) {
            return null;
        }
        return this.subInitial.getValue();
    }
    
    public ASN1OctetString getRawSubInitialValue() {
        return this.subInitial;
    }
    
    public String[] getSubAnyStrings() {
        final String[] subAnyStrings = new String[this.subAny.length];
        for (int i = 0; i < this.subAny.length; ++i) {
            subAnyStrings[i] = this.subAny[i].stringValue();
        }
        return subAnyStrings;
    }
    
    public byte[][] getSubAnyBytes() {
        final byte[][] subAnyBytes = new byte[this.subAny.length][];
        for (int i = 0; i < this.subAny.length; ++i) {
            subAnyBytes[i] = this.subAny[i].getValue();
        }
        return subAnyBytes;
    }
    
    public ASN1OctetString[] getRawSubAnyValues() {
        return this.subAny;
    }
    
    public String getSubFinalString() {
        if (this.subFinal == null) {
            return null;
        }
        return this.subFinal.stringValue();
    }
    
    public byte[] getSubFinalBytes() {
        if (this.subFinal == null) {
            return null;
        }
        return this.subFinal.getValue();
    }
    
    public ASN1OctetString getRawSubFinalValue() {
        return this.subFinal;
    }
    
    public String getMatchingRuleID() {
        return this.matchingRuleID;
    }
    
    public boolean getDNAttributes() {
        return this.dnAttributes;
    }
    
    public boolean matchesEntry(final Entry entry) throws LDAPException {
        return this.matchesEntry(entry, entry.getSchema());
    }
    
    public boolean matchesEntry(final Entry entry, final Schema schema) throws LDAPException {
        Validator.ensureNotNull(entry);
        switch (this.filterType) {
            case -96: {
                for (final Filter f : this.filterComps) {
                    if (!f.matchesEntry(entry, schema)) {
                        return false;
                    }
                }
                return true;
            }
            case -95: {
                for (final Filter f : this.filterComps) {
                    if (f.matchesEntry(entry, schema)) {
                        return true;
                    }
                }
                return false;
            }
            case -94: {
                return !this.notComp.matchesEntry(entry, schema);
            }
            case -93: {
                final Attribute a = entry.getAttribute(this.attrName, schema);
                if (a == null) {
                    return false;
                }
                final MatchingRule matchingRule = MatchingRule.selectEqualityMatchingRule(this.attrName, schema);
                return matchingRule.matchesAnyValue(this.assertionValue, a.getRawValues());
            }
            case -92: {
                final Attribute a = entry.getAttribute(this.attrName, schema);
                if (a == null) {
                    return false;
                }
                final MatchingRule matchingRule = MatchingRule.selectSubstringMatchingRule(this.attrName, schema);
                for (final ASN1OctetString v : a.getRawValues()) {
                    if (matchingRule.matchesSubstring(v, this.subInitial, this.subAny, this.subFinal)) {
                        return true;
                    }
                }
                return false;
            }
            case -91: {
                final Attribute a = entry.getAttribute(this.attrName, schema);
                if (a == null) {
                    return false;
                }
                final MatchingRule matchingRule = MatchingRule.selectOrderingMatchingRule(this.attrName, schema);
                for (final ASN1OctetString v : a.getRawValues()) {
                    if (matchingRule.compareValues(v, this.assertionValue) >= 0) {
                        return true;
                    }
                }
                return false;
            }
            case -90: {
                final Attribute a = entry.getAttribute(this.attrName, schema);
                if (a == null) {
                    return false;
                }
                final MatchingRule matchingRule = MatchingRule.selectOrderingMatchingRule(this.attrName, schema);
                for (final ASN1OctetString v : a.getRawValues()) {
                    if (matchingRule.compareValues(v, this.assertionValue) <= 0) {
                        return true;
                    }
                }
                return false;
            }
            case -121: {
                return entry.hasAttribute(this.attrName);
            }
            case -88: {
                throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_FILTER_APPROXIMATE_MATCHING_NOT_SUPPORTED.get());
            }
            case -87: {
                throw new LDAPException(ResultCode.NOT_SUPPORTED, LDAPMessages.ERR_FILTER_EXTENSIBLE_MATCHING_NOT_SUPPORTED.get());
            }
            default: {
                throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_FILTER_INVALID_TYPE.get());
            }
        }
    }
    
    public static Filter simplifyFilter(final Filter filter, final boolean reOrderElements) {
        final byte filterType = filter.filterType;
        switch (filterType) {
            case -96:
            case -95: {
                final Filter[] components = filter.filterComps;
                if (components == null || components.length == 0) {
                    return filter;
                }
                if (components.length == 1) {
                    return simplifyFilter(components[0], reOrderElements);
                }
                final LinkedHashSet<Filter> componentSet = new LinkedHashSet<Filter>(StaticUtils.computeMapCapacity(10));
                for (final Filter f : components) {
                    final Filter simplifiedFilter = simplifyFilter(f, reOrderElements);
                    if (simplifiedFilter.filterType == -96) {
                        if (filterType == -96) {
                            componentSet.addAll((Collection<?>)Arrays.asList(simplifiedFilter.filterComps));
                        }
                        else {
                            componentSet.add(simplifiedFilter);
                        }
                    }
                    else if (simplifiedFilter.filterType == -95) {
                        if (filterType == -95) {
                            componentSet.addAll((Collection<?>)Arrays.asList(simplifiedFilter.filterComps));
                        }
                        else {
                            componentSet.add(simplifiedFilter);
                        }
                    }
                    else {
                        componentSet.add(simplifiedFilter);
                    }
                }
                if (componentSet.size() == 1) {
                    return componentSet.iterator().next();
                }
                if (filterType == -96) {
                    for (final Filter f2 : componentSet) {
                        if (f2.filterType == -95 && f2.filterComps.length == 0) {
                            return f2;
                        }
                    }
                }
                else if (filterType == -95) {
                    for (final Filter f2 : componentSet) {
                        if (f2.filterType == -96 && f2.filterComps.length == 0) {
                            return f2;
                        }
                    }
                }
                if (reOrderElements) {
                    final TreeMap<Integer, LinkedHashSet<Filter>> m = new TreeMap<Integer, LinkedHashSet<Filter>>();
                    for (final Filter f3 : componentSet) {
                        Filter prioritizeComp;
                        if (f3.filterType == -96 || f3.filterType == -95) {
                            if (f3.filterComps.length > 0) {
                                prioritizeComp = f3.filterComps[0];
                            }
                            else {
                                prioritizeComp = f3;
                            }
                        }
                        else {
                            prioritizeComp = f3;
                        }
                        Integer slot = null;
                        switch (prioritizeComp.filterType) {
                            case -93: {
                                if (prioritizeComp.attrName.equalsIgnoreCase("objectClass")) {
                                    slot = 2;
                                    break;
                                }
                                slot = 1;
                                break;
                            }
                            case -88: {
                                slot = 3;
                                break;
                            }
                            case -121: {
                                if (prioritizeComp.attrName.equalsIgnoreCase("objectClass")) {
                                    slot = 9;
                                    break;
                                }
                                slot = 4;
                                break;
                            }
                            case -92: {
                                if (prioritizeComp.subInitial == null) {
                                    slot = 6;
                                    break;
                                }
                                slot = 5;
                                break;
                            }
                            case -91:
                            case -90: {
                                slot = 7;
                                break;
                            }
                            case -87: {
                                slot = 8;
                                break;
                            }
                            default: {
                                slot = 10;
                                break;
                            }
                        }
                        LinkedHashSet<Filter> filterSet = m.get(slot - 1);
                        if (filterSet == null) {
                            filterSet = new LinkedHashSet<Filter>(StaticUtils.computeMapCapacity(10));
                            m.put(slot - 1, filterSet);
                        }
                        filterSet.add(f3);
                    }
                    componentSet.clear();
                    for (final LinkedHashSet<Filter> filterSet2 : m.values()) {
                        componentSet.addAll((Collection<?>)filterSet2);
                    }
                }
                if (filterType == -96) {
                    return createANDFilter(componentSet);
                }
                return createORFilter(componentSet);
            }
            case -94: {
                return createNOTFilter(simplifyFilter(filter.notComp, reOrderElements));
            }
            default: {
                return filter;
            }
        }
    }
    
    @Override
    public int hashCode() {
        final CaseIgnoreStringMatchingRule matchingRule = CaseIgnoreStringMatchingRule.getInstance();
        int hashCode = this.filterType;
        switch (this.filterType) {
            case -96:
            case -95: {
                for (final Filter f : this.filterComps) {
                    hashCode += f.hashCode();
                }
                break;
            }
            case -94: {
                hashCode += this.notComp.hashCode();
                break;
            }
            case -93:
            case -91:
            case -90:
            case -88: {
                hashCode += StaticUtils.toLowerCase(this.attrName).hashCode();
                hashCode += matchingRule.normalize(this.assertionValue).hashCode();
                break;
            }
            case -92: {
                hashCode += StaticUtils.toLowerCase(this.attrName).hashCode();
                if (this.subInitial != null) {
                    hashCode += matchingRule.normalizeSubstring(this.subInitial, (byte)(-128)).hashCode();
                }
                for (final ASN1OctetString s : this.subAny) {
                    hashCode += matchingRule.normalizeSubstring(s, (byte)(-127)).hashCode();
                }
                if (this.subFinal != null) {
                    hashCode += matchingRule.normalizeSubstring(this.subFinal, (byte)(-126)).hashCode();
                    break;
                }
                break;
            }
            case -121: {
                hashCode += StaticUtils.toLowerCase(this.attrName).hashCode();
                break;
            }
            case -87: {
                if (this.attrName != null) {
                    hashCode += StaticUtils.toLowerCase(this.attrName).hashCode();
                }
                if (this.matchingRuleID != null) {
                    hashCode += StaticUtils.toLowerCase(this.matchingRuleID).hashCode();
                }
                if (this.dnAttributes) {
                    ++hashCode;
                }
                hashCode += matchingRule.normalize(this.assertionValue).hashCode();
                break;
            }
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof Filter)) {
            return false;
        }
        final Filter f = (Filter)o;
        if (this.filterType != f.filterType) {
            return false;
        }
        final CaseIgnoreStringMatchingRule matchingRule = CaseIgnoreStringMatchingRule.getInstance();
        switch (this.filterType) {
            case -96:
            case -95: {
                if (this.filterComps.length != f.filterComps.length) {
                    return false;
                }
                final HashSet<Filter> compSet = new HashSet<Filter>(StaticUtils.computeMapCapacity(10));
                compSet.addAll((Collection<?>)Arrays.asList(this.filterComps));
                for (final Filter filterComp : f.filterComps) {
                    if (!compSet.remove(filterComp)) {
                        return false;
                    }
                }
                return true;
            }
            case -94: {
                return this.notComp.equals(f.notComp);
            }
            case -93:
            case -91:
            case -90:
            case -88: {
                return this.attrName.equalsIgnoreCase(f.attrName) && matchingRule.valuesMatch(this.assertionValue, f.assertionValue);
            }
            case -92: {
                if (!this.attrName.equalsIgnoreCase(f.attrName)) {
                    return false;
                }
                if (this.subAny.length != f.subAny.length) {
                    return false;
                }
                if (this.subInitial == null) {
                    if (f.subInitial != null) {
                        return false;
                    }
                }
                else {
                    if (f.subInitial == null) {
                        return false;
                    }
                    final ASN1OctetString si1 = matchingRule.normalizeSubstring(this.subInitial, (byte)(-128));
                    final ASN1OctetString si2 = matchingRule.normalizeSubstring(f.subInitial, (byte)(-128));
                    if (!si1.equals(si2)) {
                        return false;
                    }
                }
                for (int i = 0; i < this.subAny.length; ++i) {
                    final ASN1OctetString sa1 = matchingRule.normalizeSubstring(this.subAny[i], (byte)(-127));
                    final ASN1OctetString sa2 = matchingRule.normalizeSubstring(f.subAny[i], (byte)(-127));
                    if (!sa1.equals(sa2)) {
                        return false;
                    }
                }
                if (this.subFinal == null) {
                    if (f.subFinal != null) {
                        return false;
                    }
                }
                else {
                    if (f.subFinal == null) {
                        return false;
                    }
                    final ASN1OctetString sf1 = matchingRule.normalizeSubstring(this.subFinal, (byte)(-126));
                    final ASN1OctetString sf2 = matchingRule.normalizeSubstring(f.subFinal, (byte)(-126));
                    if (!sf1.equals(sf2)) {
                        return false;
                    }
                }
                return true;
            }
            case -121: {
                return this.attrName.equalsIgnoreCase(f.attrName);
            }
            case -87: {
                if (this.attrName == null) {
                    if (f.attrName != null) {
                        return false;
                    }
                }
                else {
                    if (f.attrName == null) {
                        return false;
                    }
                    if (!this.attrName.equalsIgnoreCase(f.attrName)) {
                        return false;
                    }
                }
                if (this.matchingRuleID == null) {
                    if (f.matchingRuleID != null) {
                        return false;
                    }
                }
                else {
                    if (f.matchingRuleID == null) {
                        return false;
                    }
                    if (!this.matchingRuleID.equalsIgnoreCase(f.matchingRuleID)) {
                        return false;
                    }
                }
                return this.dnAttributes == f.dnAttributes && matchingRule.valuesMatch(this.assertionValue, f.assertionValue);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public String toString() {
        if (this.filterString == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toString(buffer);
            this.filterString = buffer.toString();
        }
        return this.filterString;
    }
    
    public void toString(final StringBuilder buffer) {
        switch (this.filterType) {
            case -96: {
                buffer.append("(&");
                for (final Filter f : this.filterComps) {
                    f.toString(buffer);
                }
                buffer.append(')');
                break;
            }
            case -95: {
                buffer.append("(|");
                for (final Filter f : this.filterComps) {
                    f.toString(buffer);
                }
                buffer.append(')');
                break;
            }
            case -94: {
                buffer.append("(!");
                this.notComp.toString(buffer);
                buffer.append(')');
                break;
            }
            case -93: {
                buffer.append('(');
                buffer.append(this.attrName);
                buffer.append('=');
                encodeValue(this.assertionValue, buffer);
                buffer.append(')');
                break;
            }
            case -92: {
                buffer.append('(');
                buffer.append(this.attrName);
                buffer.append('=');
                if (this.subInitial != null) {
                    encodeValue(this.subInitial, buffer);
                }
                buffer.append('*');
                for (final ASN1OctetString s : this.subAny) {
                    encodeValue(s, buffer);
                    buffer.append('*');
                }
                if (this.subFinal != null) {
                    encodeValue(this.subFinal, buffer);
                }
                buffer.append(')');
                break;
            }
            case -91: {
                buffer.append('(');
                buffer.append(this.attrName);
                buffer.append(">=");
                encodeValue(this.assertionValue, buffer);
                buffer.append(')');
                break;
            }
            case -90: {
                buffer.append('(');
                buffer.append(this.attrName);
                buffer.append("<=");
                encodeValue(this.assertionValue, buffer);
                buffer.append(')');
                break;
            }
            case -121: {
                buffer.append('(');
                buffer.append(this.attrName);
                buffer.append("=*)");
                break;
            }
            case -88: {
                buffer.append('(');
                buffer.append(this.attrName);
                buffer.append("~=");
                encodeValue(this.assertionValue, buffer);
                buffer.append(')');
                break;
            }
            case -87: {
                buffer.append('(');
                if (this.attrName != null) {
                    buffer.append(this.attrName);
                }
                if (this.dnAttributes) {
                    buffer.append(":dn");
                }
                if (this.matchingRuleID != null) {
                    buffer.append(':');
                    buffer.append(this.matchingRuleID);
                }
                buffer.append(":=");
                encodeValue(this.assertionValue, buffer);
                buffer.append(')');
                break;
            }
        }
    }
    
    public String toNormalizedString() {
        if (this.normalizedString == null) {
            final StringBuilder buffer = new StringBuilder();
            this.toNormalizedString(buffer);
            this.normalizedString = buffer.toString();
        }
        return this.normalizedString;
    }
    
    public void toNormalizedString(final StringBuilder buffer) {
        final CaseIgnoreStringMatchingRule mr = CaseIgnoreStringMatchingRule.getInstance();
        switch (this.filterType) {
            case -96: {
                buffer.append("(&");
                for (final Filter f : this.filterComps) {
                    f.toNormalizedString(buffer);
                }
                buffer.append(')');
                break;
            }
            case -95: {
                buffer.append("(|");
                for (final Filter f : this.filterComps) {
                    f.toNormalizedString(buffer);
                }
                buffer.append(')');
                break;
            }
            case -94: {
                buffer.append("(!");
                this.notComp.toNormalizedString(buffer);
                buffer.append(')');
                break;
            }
            case -93: {
                buffer.append('(');
                buffer.append(StaticUtils.toLowerCase(this.attrName));
                buffer.append('=');
                encodeValue(mr.normalize(this.assertionValue), buffer);
                buffer.append(')');
                break;
            }
            case -92: {
                buffer.append('(');
                buffer.append(StaticUtils.toLowerCase(this.attrName));
                buffer.append('=');
                if (this.subInitial != null) {
                    encodeValue(mr.normalizeSubstring(this.subInitial, (byte)(-128)), buffer);
                }
                buffer.append('*');
                for (final ASN1OctetString s : this.subAny) {
                    encodeValue(mr.normalizeSubstring(s, (byte)(-127)), buffer);
                    buffer.append('*');
                }
                if (this.subFinal != null) {
                    encodeValue(mr.normalizeSubstring(this.subFinal, (byte)(-126)), buffer);
                }
                buffer.append(')');
                break;
            }
            case -91: {
                buffer.append('(');
                buffer.append(StaticUtils.toLowerCase(this.attrName));
                buffer.append(">=");
                encodeValue(mr.normalize(this.assertionValue), buffer);
                buffer.append(')');
                break;
            }
            case -90: {
                buffer.append('(');
                buffer.append(StaticUtils.toLowerCase(this.attrName));
                buffer.append("<=");
                encodeValue(mr.normalize(this.assertionValue), buffer);
                buffer.append(')');
                break;
            }
            case -121: {
                buffer.append('(');
                buffer.append(StaticUtils.toLowerCase(this.attrName));
                buffer.append("=*)");
                break;
            }
            case -88: {
                buffer.append('(');
                buffer.append(StaticUtils.toLowerCase(this.attrName));
                buffer.append("~=");
                encodeValue(mr.normalize(this.assertionValue), buffer);
                buffer.append(')');
                break;
            }
            case -87: {
                buffer.append('(');
                if (this.attrName != null) {
                    buffer.append(StaticUtils.toLowerCase(this.attrName));
                }
                if (this.dnAttributes) {
                    buffer.append(":dn");
                }
                if (this.matchingRuleID != null) {
                    buffer.append(':');
                    buffer.append(StaticUtils.toLowerCase(this.matchingRuleID));
                }
                buffer.append(":=");
                encodeValue(mr.normalize(this.assertionValue), buffer);
                buffer.append(')');
                break;
            }
        }
    }
    
    public static String encodeValue(final String value) {
        Validator.ensureNotNull(value);
        final StringBuilder buffer = new StringBuilder();
        encodeValue(new ASN1OctetString(value), buffer);
        return buffer.toString();
    }
    
    public static String encodeValue(final byte[] value) {
        Validator.ensureNotNull(value);
        final StringBuilder buffer = new StringBuilder();
        encodeValue(new ASN1OctetString(value), buffer);
        return buffer.toString();
    }
    
    public static void encodeValue(final ASN1OctetString value, final StringBuilder buffer) {
        final byte[] valueBytes = value.getValue();
        for (int i = 0; i < valueBytes.length; ++i) {
            switch (StaticUtils.numBytesInUTF8CharacterWithFirstByte(valueBytes[i])) {
                case 1: {
                    if (valueBytes[i] <= 31 || valueBytes[i] == 40 || valueBytes[i] == 41 || valueBytes[i] == 42 || valueBytes[i] == 92 || valueBytes[i] == 127) {
                        buffer.append('\\');
                        StaticUtils.toHex(valueBytes[i], buffer);
                        break;
                    }
                    buffer.append((char)valueBytes[i]);
                    break;
                }
                case 2: {
                    buffer.append('\\');
                    StaticUtils.toHex(valueBytes[i++], buffer);
                    if (i < valueBytes.length) {
                        buffer.append('\\');
                        StaticUtils.toHex(valueBytes[i], buffer);
                        break;
                    }
                    break;
                }
                case 3: {
                    buffer.append('\\');
                    StaticUtils.toHex(valueBytes[i++], buffer);
                    if (i < valueBytes.length) {
                        buffer.append('\\');
                        StaticUtils.toHex(valueBytes[i++], buffer);
                    }
                    if (i < valueBytes.length) {
                        buffer.append('\\');
                        StaticUtils.toHex(valueBytes[i], buffer);
                        break;
                    }
                    break;
                }
                case 4: {
                    buffer.append('\\');
                    StaticUtils.toHex(valueBytes[i++], buffer);
                    if (i < valueBytes.length) {
                        buffer.append('\\');
                        StaticUtils.toHex(valueBytes[i++], buffer);
                    }
                    if (i < valueBytes.length) {
                        buffer.append('\\');
                        StaticUtils.toHex(valueBytes[i++], buffer);
                    }
                    if (i < valueBytes.length) {
                        buffer.append('\\');
                        StaticUtils.toHex(valueBytes[i], buffer);
                        break;
                    }
                    break;
                }
                default: {
                    while (i < valueBytes.length) {
                        buffer.append('\\');
                        StaticUtils.toHex(valueBytes[i++], buffer);
                    }
                    break;
                }
            }
        }
    }
    
    public void toCode(final List<String> lineList, final int indentSpaces, final String firstLinePrefix, final String lastLineSuffix) {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < indentSpaces; ++i) {
            buffer.append(' ');
        }
        final String indent = buffer.toString();
        buffer.setLength(0);
        buffer.append(indent);
        if (firstLinePrefix != null) {
            buffer.append(firstLinePrefix);
        }
        switch (this.filterType) {
            case -96:
            case -95: {
                if (this.filterType == -96) {
                    buffer.append("Filter.createANDFilter(");
                }
                else {
                    buffer.append("Filter.createORFilter(");
                }
                if (this.filterComps.length == 0) {
                    buffer.append(')');
                    if (lastLineSuffix != null) {
                        buffer.append(lastLineSuffix);
                    }
                    lineList.add(buffer.toString());
                    return;
                }
                for (int j = 0; j < this.filterComps.length; ++j) {
                    String suffix;
                    if (j == this.filterComps.length - 1) {
                        suffix = ")";
                        if (lastLineSuffix != null) {
                            suffix += lastLineSuffix;
                        }
                    }
                    else {
                        suffix = ",";
                    }
                    this.filterComps[j].toCode(lineList, indentSpaces + 5, null, suffix);
                }
                return;
            }
            case -94: {
                buffer.append("Filter.createNOTFilter(");
                lineList.add(buffer.toString());
                String suffix2;
                if (lastLineSuffix == null) {
                    suffix2 = ")";
                }
                else {
                    suffix2 = ')' + lastLineSuffix;
                }
                this.notComp.toCode(lineList, indentSpaces + 5, null, suffix2);
                return;
            }
            case -121: {
                buffer.append("Filter.createPresenceFilter(");
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     \"");
                buffer.append(this.attrName);
                buffer.append("\")");
                if (lastLineSuffix != null) {
                    buffer.append(lastLineSuffix);
                }
                lineList.add(buffer.toString());
                return;
            }
            case -93:
            case -91:
            case -90:
            case -88: {
                if (this.filterType == -93) {
                    buffer.append("Filter.createEqualityFilter(");
                }
                else if (this.filterType == -91) {
                    buffer.append("Filter.createGreaterOrEqualFilter(");
                }
                else if (this.filterType == -90) {
                    buffer.append("Filter.createLessOrEqualFilter(");
                }
                else {
                    buffer.append("Filter.createApproximateMatchFilter(");
                }
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     \"");
                buffer.append(this.attrName);
                buffer.append("\",");
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                if (StaticUtils.isSensitiveToCodeAttribute(this.attrName)) {
                    buffer.append("\"---redacted-value---\"");
                }
                else if (StaticUtils.isPrintableString(this.assertionValue.getValue())) {
                    buffer.append('\"');
                    buffer.append(this.assertionValue.stringValue());
                    buffer.append('\"');
                }
                else {
                    StaticUtils.byteArrayToCode(this.assertionValue.getValue(), buffer);
                }
                buffer.append(')');
                if (lastLineSuffix != null) {
                    buffer.append(lastLineSuffix);
                }
                lineList.add(buffer.toString());
                return;
            }
            case -92: {
                buffer.append("Filter.createSubstringFilter(");
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     \"");
                buffer.append(this.attrName);
                buffer.append("\",");
                lineList.add(buffer.toString());
                final boolean isRedacted = StaticUtils.isSensitiveToCodeAttribute(this.attrName);
                boolean isPrintable = true;
                if (this.subInitial != null) {
                    isPrintable = StaticUtils.isPrintableString(this.subInitial.getValue());
                }
                if (isPrintable && this.subAny != null) {
                    for (final ASN1OctetString s : this.subAny) {
                        if (!StaticUtils.isPrintableString(s.getValue())) {
                            isPrintable = false;
                            break;
                        }
                    }
                }
                if (isPrintable && this.subFinal != null) {
                    isPrintable = StaticUtils.isPrintableString(this.subFinal.getValue());
                }
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                if (this.subInitial == null) {
                    buffer.append("null");
                }
                else if (isRedacted) {
                    buffer.append("\"---redacted-subInitial---\"");
                }
                else if (isPrintable) {
                    buffer.append('\"');
                    buffer.append(this.subInitial.stringValue());
                    buffer.append('\"');
                }
                else {
                    StaticUtils.byteArrayToCode(this.subInitial.getValue(), buffer);
                }
                buffer.append(',');
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                if (this.subAny == null || this.subAny.length == 0) {
                    buffer.append("null,");
                    lineList.add(buffer.toString());
                }
                else if (isRedacted) {
                    buffer.append("new String[]");
                    lineList.add(buffer.toString());
                    lineList.add(indent + "     {");
                    for (int k = 0; k < this.subAny.length; ++k) {
                        buffer.setLength(0);
                        buffer.append(indent);
                        buffer.append("       \"---redacted-subAny-");
                        buffer.append(k + 1);
                        buffer.append("---\"");
                        if (k < this.subAny.length - 1) {
                            buffer.append(',');
                        }
                        lineList.add(buffer.toString());
                    }
                    lineList.add(indent + "     },");
                }
                else if (isPrintable) {
                    buffer.append("new String[]");
                    lineList.add(buffer.toString());
                    lineList.add(indent + "     {");
                    for (int k = 0; k < this.subAny.length; ++k) {
                        buffer.setLength(0);
                        buffer.append(indent);
                        buffer.append("       \"");
                        buffer.append(this.subAny[k].stringValue());
                        buffer.append('\"');
                        if (k < this.subAny.length - 1) {
                            buffer.append(',');
                        }
                        lineList.add(buffer.toString());
                    }
                    lineList.add(indent + "     },");
                }
                else {
                    buffer.append("new String[]");
                    lineList.add(buffer.toString());
                    lineList.add(indent + "     {");
                    for (int k = 0; k < this.subAny.length; ++k) {
                        buffer.setLength(0);
                        buffer.append(indent);
                        buffer.append("       ");
                        StaticUtils.byteArrayToCode(this.subAny[k].getValue(), buffer);
                        if (k < this.subAny.length - 1) {
                            buffer.append(',');
                        }
                        lineList.add(buffer.toString());
                    }
                    lineList.add(indent + "     },");
                }
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                if (this.subFinal == null) {
                    buffer.append("null)");
                }
                else if (isRedacted) {
                    buffer.append("\"---redacted-subFinal---\")");
                }
                else if (isPrintable) {
                    buffer.append('\"');
                    buffer.append(this.subFinal.stringValue());
                    buffer.append("\")");
                }
                else {
                    StaticUtils.byteArrayToCode(this.subFinal.getValue(), buffer);
                    buffer.append(')');
                }
                if (lastLineSuffix != null) {
                    buffer.append(lastLineSuffix);
                }
                lineList.add(buffer.toString());
                return;
            }
            case -87: {
                buffer.append("Filter.createExtensibleMatchFilter(");
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                if (this.attrName == null) {
                    buffer.append("null, // Attribute Description");
                }
                else {
                    buffer.append('\"');
                    buffer.append(this.attrName);
                    buffer.append("\",");
                }
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                if (this.matchingRuleID == null) {
                    buffer.append("null, // Matching Rule ID");
                }
                else {
                    buffer.append('\"');
                    buffer.append(this.matchingRuleID);
                    buffer.append("\",");
                }
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                buffer.append(this.dnAttributes);
                buffer.append(", // DN Attributes");
                lineList.add(buffer.toString());
                buffer.setLength(0);
                buffer.append(indent);
                buffer.append("     ");
                if (this.attrName != null && StaticUtils.isSensitiveToCodeAttribute(this.attrName)) {
                    buffer.append("\"---redacted-value---\")");
                }
                else if (StaticUtils.isPrintableString(this.assertionValue.getValue())) {
                    buffer.append('\"');
                    buffer.append(this.assertionValue.stringValue());
                    buffer.append("\")");
                }
                else {
                    StaticUtils.byteArrayToCode(this.assertionValue.getValue(), buffer);
                    buffer.append(')');
                }
                if (lastLineSuffix != null) {
                    buffer.append(lastLineSuffix);
                }
                lineList.add(buffer.toString());
            }
            default: {}
        }
    }
    
    static {
        NO_FILTERS = new Filter[0];
        NO_SUB_ANY = new ASN1OctetString[0];
    }
}
