package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CaseIgnoreStringMatchingRule extends AcceptAllSimpleMatchingRule
{
    private static final CaseIgnoreStringMatchingRule INSTANCE;
    public static final String EQUALITY_RULE_NAME = "caseIgnoreMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.2";
    public static final String ORDERING_RULE_NAME = "caseIgnoreOrderingMatch";
    static final String LOWER_ORDERING_RULE_NAME;
    public static final String ORDERING_RULE_OID = "2.5.13.3";
    public static final String SUBSTRING_RULE_NAME = "caseIgnoreSubstringsMatch";
    static final String LOWER_SUBSTRING_RULE_NAME;
    public static final String SUBSTRING_RULE_OID = "2.5.13.4";
    private static final long serialVersionUID = -1293370922676445525L;
    
    public static CaseIgnoreStringMatchingRule getInstance() {
        return CaseIgnoreStringMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "caseIgnoreMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.2";
    }
    
    @Override
    public String getOrderingMatchingRuleName() {
        return "caseIgnoreOrderingMatch";
    }
    
    @Override
    public String getOrderingMatchingRuleOID() {
        return "2.5.13.3";
    }
    
    @Override
    public String getSubstringMatchingRuleName() {
        return "caseIgnoreSubstringsMatch";
    }
    
    @Override
    public String getSubstringMatchingRuleOID() {
        return "2.5.13.4";
    }
    
    @Override
    public boolean valuesMatch(final ASN1OctetString value1, final ASN1OctetString value2) {
        final byte[] value1Bytes = value1.getValue();
        final byte[] value2Bytes = value2.getValue();
        if (value1Bytes.length == value2Bytes.length) {
            for (int i = 0; i < value1Bytes.length; ++i) {
                final byte b1 = value1Bytes[i];
                final byte b2 = value2Bytes[i];
                if ((b1 & 0x7F) != (b1 & 0xFF) || (b2 & 0x7F) != (b2 & 0xFF)) {
                    return this.normalize(value1).equals(this.normalize(value2));
                }
                if (b1 != b2) {
                    if (b1 == 32 || b2 == 32) {
                        return this.normalize(value1).equals(this.normalize(value2));
                    }
                    if (Character.isUpperCase((char)b1)) {
                        final char c = Character.toLowerCase((char)b1);
                        if (c != (char)b2) {
                            return false;
                        }
                    }
                    else {
                        if (!Character.isUpperCase((char)b2)) {
                            return false;
                        }
                        final char c = Character.toLowerCase((char)b2);
                        if (c != (char)b1) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return normalizeInternal(value1, false, (byte)0).equals(normalizeInternal(value2, false, (byte)0));
    }
    
    @Override
    public ASN1OctetString normalize(final ASN1OctetString value) {
        return normalizeInternal(value, false, (byte)0);
    }
    
    @Override
    public ASN1OctetString normalizeSubstring(final ASN1OctetString value, final byte substringType) {
        return normalizeInternal(value, true, substringType);
    }
    
    private static ASN1OctetString normalizeInternal(final ASN1OctetString value, final boolean isSubstring, final byte substringType) {
        final byte[] valueBytes = value.getValue();
        if (valueBytes.length == 0) {
            return value;
        }
        boolean trimInitial = false;
        boolean trimFinal = false;
        if (isSubstring) {
            switch (substringType) {
                case Byte.MIN_VALUE: {
                    trimInitial = true;
                    trimFinal = false;
                    break;
                }
                case -126: {
                    trimInitial = false;
                    trimFinal = true;
                    break;
                }
                default: {
                    trimInitial = false;
                    trimFinal = false;
                    break;
                }
            }
        }
        else {
            trimInitial = true;
            trimFinal = true;
        }
        boolean containsNonSpace = false;
        boolean lastWasSpace = trimInitial;
        int numDuplicates = 0;
        for (final byte b : valueBytes) {
            if ((b & 0x7F) != (b & 0xFF)) {
                return normalizeNonASCII(value, trimInitial, trimFinal);
            }
            if (b == 32) {
                if (lastWasSpace) {
                    ++numDuplicates;
                }
                else {
                    lastWasSpace = true;
                }
            }
            else {
                containsNonSpace = true;
                lastWasSpace = false;
            }
        }
        if (!containsNonSpace) {
            return new ASN1OctetString(" ");
        }
        if (lastWasSpace && trimFinal) {
            ++numDuplicates;
        }
        lastWasSpace = trimInitial;
        int targetPos = 0;
        final byte[] normalizedBytes = new byte[valueBytes.length - numDuplicates];
        for (int i = 0; i < valueBytes.length; ++i) {
            switch (valueBytes[i]) {
                case 32: {
                    if (lastWasSpace) {
                        break;
                    }
                    if (trimFinal && i == valueBytes.length - 1) {
                        break;
                    }
                    if (targetPos < normalizedBytes.length) {
                        normalizedBytes[targetPos++] = 32;
                        lastWasSpace = true;
                        break;
                    }
                    break;
                }
                case 65: {
                    normalizedBytes[targetPos++] = 97;
                    lastWasSpace = false;
                    break;
                }
                case 66: {
                    normalizedBytes[targetPos++] = 98;
                    lastWasSpace = false;
                    break;
                }
                case 67: {
                    normalizedBytes[targetPos++] = 99;
                    lastWasSpace = false;
                    break;
                }
                case 68: {
                    normalizedBytes[targetPos++] = 100;
                    lastWasSpace = false;
                    break;
                }
                case 69: {
                    normalizedBytes[targetPos++] = 101;
                    lastWasSpace = false;
                    break;
                }
                case 70: {
                    normalizedBytes[targetPos++] = 102;
                    lastWasSpace = false;
                    break;
                }
                case 71: {
                    normalizedBytes[targetPos++] = 103;
                    lastWasSpace = false;
                    break;
                }
                case 72: {
                    normalizedBytes[targetPos++] = 104;
                    lastWasSpace = false;
                    break;
                }
                case 73: {
                    normalizedBytes[targetPos++] = 105;
                    lastWasSpace = false;
                    break;
                }
                case 74: {
                    normalizedBytes[targetPos++] = 106;
                    lastWasSpace = false;
                    break;
                }
                case 75: {
                    normalizedBytes[targetPos++] = 107;
                    lastWasSpace = false;
                    break;
                }
                case 76: {
                    normalizedBytes[targetPos++] = 108;
                    lastWasSpace = false;
                    break;
                }
                case 77: {
                    normalizedBytes[targetPos++] = 109;
                    lastWasSpace = false;
                    break;
                }
                case 78: {
                    normalizedBytes[targetPos++] = 110;
                    lastWasSpace = false;
                    break;
                }
                case 79: {
                    normalizedBytes[targetPos++] = 111;
                    lastWasSpace = false;
                    break;
                }
                case 80: {
                    normalizedBytes[targetPos++] = 112;
                    lastWasSpace = false;
                    break;
                }
                case 81: {
                    normalizedBytes[targetPos++] = 113;
                    lastWasSpace = false;
                    break;
                }
                case 82: {
                    normalizedBytes[targetPos++] = 114;
                    lastWasSpace = false;
                    break;
                }
                case 83: {
                    normalizedBytes[targetPos++] = 115;
                    lastWasSpace = false;
                    break;
                }
                case 84: {
                    normalizedBytes[targetPos++] = 116;
                    lastWasSpace = false;
                    break;
                }
                case 85: {
                    normalizedBytes[targetPos++] = 117;
                    lastWasSpace = false;
                    break;
                }
                case 86: {
                    normalizedBytes[targetPos++] = 118;
                    lastWasSpace = false;
                    break;
                }
                case 87: {
                    normalizedBytes[targetPos++] = 119;
                    lastWasSpace = false;
                    break;
                }
                case 88: {
                    normalizedBytes[targetPos++] = 120;
                    lastWasSpace = false;
                    break;
                }
                case 89: {
                    normalizedBytes[targetPos++] = 121;
                    lastWasSpace = false;
                    break;
                }
                case 90: {
                    normalizedBytes[targetPos++] = 122;
                    lastWasSpace = false;
                    break;
                }
                default: {
                    normalizedBytes[targetPos++] = valueBytes[i];
                    lastWasSpace = false;
                    break;
                }
            }
        }
        return new ASN1OctetString(normalizedBytes);
    }
    
    private static ASN1OctetString normalizeNonASCII(final ASN1OctetString value, final boolean trimInitial, final boolean trimFinal) {
        final StringBuilder buffer = new StringBuilder(value.stringValue());
        int pos = 0;
        boolean lastWasSpace = trimInitial;
        while (pos < buffer.length()) {
            final char c = buffer.charAt(pos++);
            if (c == ' ') {
                if (lastWasSpace || (trimFinal && pos >= buffer.length())) {
                    buffer.deleteCharAt(--pos);
                }
                else {
                    lastWasSpace = true;
                }
            }
            else {
                if (Character.isHighSurrogate(c)) {
                    if (pos < buffer.length()) {
                        final char c2 = buffer.charAt(pos++);
                        if (Character.isLowSurrogate(c2)) {
                            final int codePoint = Character.toCodePoint(c, c2);
                            if (Character.isUpperCase(codePoint)) {
                                final int lowerCaseCodePoint = Character.toLowerCase(codePoint);
                                buffer.setCharAt(pos - 2, Character.highSurrogate(lowerCaseCodePoint));
                                buffer.setCharAt(pos - 1, Character.lowSurrogate(lowerCaseCodePoint));
                            }
                        }
                    }
                }
                else if (Character.isUpperCase(c)) {
                    buffer.setCharAt(pos - 1, Character.toLowerCase(c));
                }
                lastWasSpace = false;
            }
        }
        if (trimFinal && buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == ' ') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return new ASN1OctetString(buffer.toString());
    }
    
    static {
        INSTANCE = new CaseIgnoreStringMatchingRule();
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("caseIgnoreMatch");
        LOWER_ORDERING_RULE_NAME = StaticUtils.toLowerCase("caseIgnoreOrderingMatch");
        LOWER_SUBSTRING_RULE_NAME = StaticUtils.toLowerCase("caseIgnoreSubstringsMatch");
    }
}
