package com.unboundid.ldap.matchingrules;

import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class CaseExactStringMatchingRule extends AcceptAllSimpleMatchingRule
{
    private static final CaseExactStringMatchingRule INSTANCE;
    public static final String EQUALITY_RULE_NAME = "caseExactMatch";
    static final String LOWER_EQUALITY_RULE_NAME;
    public static final String EQUALITY_RULE_OID = "2.5.13.5";
    public static final String ORDERING_RULE_NAME = "caseExactOrderingMatch";
    static final String LOWER_ORDERING_RULE_NAME;
    public static final String ORDERING_RULE_OID = "2.5.13.6";
    public static final String SUBSTRING_RULE_NAME = "caseExactSubstringsMatch";
    static final String LOWER_SUBSTRING_RULE_NAME;
    public static final String SUBSTRING_RULE_OID = "2.5.13.7";
    private static final long serialVersionUID = -6336492464430413364L;
    
    public static CaseExactStringMatchingRule getInstance() {
        return CaseExactStringMatchingRule.INSTANCE;
    }
    
    @Override
    public String getEqualityMatchingRuleName() {
        return "caseExactMatch";
    }
    
    @Override
    public String getEqualityMatchingRuleOID() {
        return "2.5.13.5";
    }
    
    @Override
    public String getOrderingMatchingRuleName() {
        return "caseExactOrderingMatch";
    }
    
    @Override
    public String getOrderingMatchingRuleOID() {
        return "2.5.13.6";
    }
    
    @Override
    public String getSubstringMatchingRuleName() {
        return "caseExactSubstringsMatch";
    }
    
    @Override
    public String getSubstringMatchingRuleOID() {
        return "2.5.13.7";
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
                    return (b1 == 32 || b2 == 32) && this.normalize(value1).equals(this.normalize(value2));
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
            if (valueBytes[i] == 32) {
                if (!lastWasSpace) {
                    if (!trimFinal || i != valueBytes.length - 1) {
                        if (targetPos < normalizedBytes.length) {
                            normalizedBytes[targetPos++] = 32;
                            lastWasSpace = true;
                        }
                    }
                }
            }
            else {
                normalizedBytes[targetPos++] = valueBytes[i];
                lastWasSpace = false;
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
                lastWasSpace = false;
            }
        }
        if (trimFinal && buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == ' ') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return new ASN1OctetString(buffer.toString());
    }
    
    static {
        INSTANCE = new CaseExactStringMatchingRule();
        LOWER_EQUALITY_RULE_NAME = StaticUtils.toLowerCase("caseExactMatch");
        LOWER_ORDERING_RULE_NAME = StaticUtils.toLowerCase("caseExactOrderingMatch");
        LOWER_SUBSTRING_RULE_NAME = StaticUtils.toLowerCase("caseExactSubstringsMatch");
    }
}
