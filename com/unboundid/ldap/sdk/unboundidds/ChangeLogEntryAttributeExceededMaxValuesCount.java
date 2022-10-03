package com.unboundid.ldap.sdk.unboundidds;

import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.StringTokenizer;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ChangeLogEntryAttributeExceededMaxValuesCount implements Serializable
{
    private static final String TOKEN_NAME_ATTR = "attr";
    private static final String TOKEN_NAME_BEFORE_COUNT;
    private static final String TOKEN_NAME_AFTER_COUNT;
    private static final long serialVersionUID = -4689107630879614032L;
    private final long afterCount;
    private final long beforeCount;
    private final String attributeName;
    private final String stringRepresentation;
    
    public ChangeLogEntryAttributeExceededMaxValuesCount(final String s) throws LDAPException {
        this.stringRepresentation = s;
        String name = null;
        Long before = null;
        Long after = null;
        final StringTokenizer tokenizer = new StringTokenizer(s, ",");
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            final int equalPos = token.indexOf(61);
            if (equalPos < 0) {
                throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_MALFORMED_TOKEN.get(s, token));
            }
            final String tokenName = StaticUtils.toLowerCase(token.substring(0, equalPos).trim());
            final String value = token.substring(equalPos + 1).trim();
            if (tokenName.equals("attr")) {
                if (name != null) {
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_REPEATED_TOKEN.get(s, tokenName));
                }
                name = value;
            }
            else {
                if (tokenName.equals(ChangeLogEntryAttributeExceededMaxValuesCount.TOKEN_NAME_BEFORE_COUNT)) {
                    if (before == null) {
                        try {
                            before = Long.parseLong(value);
                            continue;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_MALFORMED_COUNT.get(s, tokenName), e);
                        }
                    }
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_REPEATED_TOKEN.get(s, tokenName));
                }
                if (tokenName.equals(ChangeLogEntryAttributeExceededMaxValuesCount.TOKEN_NAME_AFTER_COUNT)) {
                    if (after == null) {
                        try {
                            after = Long.parseLong(value);
                            continue;
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_REPEATED_TOKEN.get(s, tokenName), e);
                        }
                    }
                    throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_REPEATED_TOKEN.get(s, tokenName));
                }
                continue;
            }
        }
        if (name == null) {
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_MISSING_TOKEN.get(s, "attr"));
        }
        if (before == null) {
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_MISSING_TOKEN.get(s, ChangeLogEntryAttributeExceededMaxValuesCount.TOKEN_NAME_BEFORE_COUNT));
        }
        if (after == null) {
            throw new LDAPException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, UnboundIDDSMessages.ERR_CHANGELOG_EXCEEDED_VALUE_COUNT_MISSING_TOKEN.get(s, ChangeLogEntryAttributeExceededMaxValuesCount.TOKEN_NAME_AFTER_COUNT));
        }
        this.attributeName = name;
        this.beforeCount = before;
        this.afterCount = after;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public long getBeforeCount() {
        return this.beforeCount;
    }
    
    public long getAfterCount() {
        return this.afterCount;
    }
    
    @Override
    public int hashCode() {
        int hashCode = StaticUtils.toLowerCase(this.attributeName).hashCode();
        hashCode = (int)(hashCode * 31 + this.beforeCount);
        hashCode = (int)(hashCode * 31 + this.afterCount);
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
        if (!(o instanceof ChangeLogEntryAttributeExceededMaxValuesCount)) {
            return false;
        }
        final ChangeLogEntryAttributeExceededMaxValuesCount c = (ChangeLogEntryAttributeExceededMaxValuesCount)o;
        return this.beforeCount == c.beforeCount && this.afterCount == c.afterCount && this.attributeName.equalsIgnoreCase(c.attributeName);
    }
    
    @Override
    public String toString() {
        return this.stringRepresentation;
    }
    
    static {
        TOKEN_NAME_BEFORE_COUNT = StaticUtils.toLowerCase("beforeCount");
        TOKEN_NAME_AFTER_COUNT = StaticUtils.toLowerCase("afterCount");
    }
}
