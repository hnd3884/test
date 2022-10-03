package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import java.util.Collections;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CaseIgnoreListMatchingRule extends MatchingRule
{
    private static final CaseIgnoreListMatchingRule INSTANCE;
    public static final String EQUALITY_RULE_NAME = "caseIgnoreListMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.11";
    public static final String SUBSTRING_RULE_NAME = "caseIgnoreListSubstringsMatch";
    static final String LOWER_SUBSTRING_RULE_NAME;
    public static final String SUBSTRING_RULE_OID = "2.5.13.12";
    private static final long serialVersionUID = 7795143670808983466L;
    
    public static CaseIgnoreListMatchingRule getInstance() {
        return CaseIgnoreListMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "caseIgnoreListMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.11";
    }
    
    @Override
    public String getOrderingMatchingRuleName() {
        return null;
    }
    
    @Override
    public String getOrderingMatchingRuleOID() {
        return null;
    }
    
    @Override
    public String getSubstringMatchingRuleName() {
        return "caseIgnoreListSubstringsMatch";
    }
    
    @Override
    public String getSubstringMatchingRuleOID() {
        return "2.5.13.12";
    }
    
    @Override
    public boolean valuesMatch(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        return this.normalize(value1).equals(this.normalize(value2));
    }
    
    @Override
    public boolean matchesSubstring(final ASN1OctetString value, final ASN1OctetString subInitial, final ASN1OctetString[] subAny, final ASN1OctetString subFinal) throws LDAPException {
        String normStr = this.normalize(value).stringValue();
        if (subInitial != null) {
            final String normSubInitial = this.normalizeSubstring(subInitial, (byte)(-128)).stringValue();
            if (normSubInitial.indexOf(36) >= 0) {
                throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_SUBSTRING_COMPONENT_CONTAINS_DOLLAR.get(normSubInitial));
            }
            if (!normStr.startsWith(normSubInitial)) {
                return false;
            }
            normStr = normStr.substring(normSubInitial.length());
        }
        if (subFinal != null) {
            final String normSubFinal = this.normalizeSubstring(subFinal, (byte)(-126)).stringValue();
            if (normSubFinal.indexOf(36) >= 0) {
                throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_SUBSTRING_COMPONENT_CONTAINS_DOLLAR.get(normSubFinal));
            }
            if (!normStr.endsWith(normSubFinal)) {
                return false;
            }
            normStr = normStr.substring(0, normStr.length() - normSubFinal.length());
        }
        if (subAny != null) {
            for (final ASN1OctetString s : subAny) {
                final String normSubAny = this.normalizeSubstring(s, (byte)(-127)).stringValue();
                if (normSubAny.indexOf(36) >= 0) {
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_SUBSTRING_COMPONENT_CONTAINS_DOLLAR.get(normSubAny));
                }
                final int pos = normStr.indexOf(normSubAny);
                if (pos < 0) {
                    return false;
                }
                normStr = normStr.substring(pos + normSubAny.length());
            }
        }
        return true;
    }
    
    @Override
    public int compareValues(final ASN1OctetString value1, final ASN1OctetString value2) throws LDAPException {
        throw new LDAPException(ResultCode.INAPPROPRIATE_MATCHING, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_ORDERING_MATCHING_NOT_SUPPORTED.get());
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) throws LDAPException {
        final List<String> items = getLowercaseItems(value);
        final Iterator<String> iterator = items.iterator();
        final StringBuilder buffer = new StringBuilder();
        while (iterator.hasNext()) {
            normalizeItem(buffer, iterator.next());
            if (iterator.hasNext()) {
                buffer.append('$');
            }
        }
        return new ASN1OctetString(buffer.toString());
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) throws LDAPException {
        return CaseIgnoreStringMatchingRule.getInstance().normalizeSubstring(value, substringType);
    }
    
    public static List<String> getItems(final ASN1OctetString value) throws LDAPException {
        return getItems(value.stringValue());
    }
    
    public static List<String> getItems(final String value) throws LDAPException {
        final ArrayList<String> items = new ArrayList<String>(10);
        final int length = value.length();
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            final char c = value.charAt(i);
            if (c == '\\') {
                try {
                    buffer.append(decodeHexChar(value, i + 1));
                    i += 2;
                    continue;
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_MALFORMED_HEX_CHAR.get(value), e);
                }
            }
            if (c == '$') {
                final String s = buffer.toString().trim();
                if (s.length() == 0) {
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_EMPTY_ITEM.get(value));
                }
                items.add(s);
                buffer.delete(0, buffer.length());
            }
            else {
                buffer.append(c);
            }
        }
        final String s2 = buffer.toString().trim();
        if (s2.length() != 0) {
            items.add(s2);
            return Collections.unmodifiableList((List<? extends String>)items);
        }
        if (items.isEmpty()) {
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_EMPTY_LIST.get(value));
        }
        throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_EMPTY_ITEM.get(value));
    }
    
    public static List<String> getLowercaseItems(final ASN1OctetString value) throws LDAPException {
        return getLowercaseItems(value.stringValue());
    }
    
    public static List<String> getLowercaseItems(final String value) throws LDAPException {
        return getItems(StaticUtils.toLowerCase(value));
    }
    
    static void normalizeItem(final StringBuilder buffer, final String item) {
        final int length = item.length();
        boolean lastWasSpace = false;
        for (int i = 0; i < length; ++i) {
            final char c = item.charAt(i);
            if (c == '\\') {
                buffer.append("\\5c");
                lastWasSpace = false;
            }
            else if (c == '$') {
                buffer.append("\\24");
                lastWasSpace = false;
            }
            else if (c == ' ') {
                if (!lastWasSpace) {
                    buffer.append(' ');
                    lastWasSpace = true;
                }
            }
            else {
                buffer.append(c);
                lastWasSpace = false;
            }
        }
    }
    
    static char decodeHexChar(final String s, final int p) throws LDAPException {
        char c = '\0';
        for (int i = 0, j = p; i < 2; ++i, ++j) {
            c <<= 4;
            switch (s.charAt(j)) {
                case '0': {
                    break;
                }
                case '1': {
                    c |= '\u0001';
                    break;
                }
                case '2': {
                    c |= '\u0002';
                    break;
                }
                case '3': {
                    c |= '\u0003';
                    break;
                }
                case '4': {
                    c |= '\u0004';
                    break;
                }
                case '5': {
                    c |= '\u0005';
                    break;
                }
                case '6': {
                    c |= '\u0006';
                    break;
                }
                case '7': {
                    c |= '\u0007';
                    break;
                }
                case '8': {
                    c |= '\b';
                    break;
                }
                case '9': {
                    c |= '\t';
                    break;
                }
                case 'A':
                case 'a': {
                    c |= '\n';
                    break;
                }
                case 'B':
                case 'b': {
                    c |= '\u000b';
                    break;
                }
                case 'C':
                case 'c': {
                    c |= '\f';
                    break;
                }
                case 'D':
                case 'd': {
                    c |= '\r';
                    break;
                }
                case 'E':
                case 'e': {
                    c |= '\u000e';
                    break;
                }
                case 'F':
                case 'f': {
                    c |= '\u000f';
                    break;
                }
                default: {
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, MatchingRuleMessages.ERR_CASE_IGNORE_LIST_NOT_HEX_DIGIT.get(s.charAt(j)));
                }
            }
        }
        return c;
    }
    
    static {
        INSTANCE = new CaseIgnoreListMatchingRule();
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("caseIgnoreListMatch");
        LOWER_SUBSTRING_RULE_NAME = StaticUtils.toLowerCase("caseIgnoreListSubstringsMatch");
    }
}
