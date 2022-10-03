package com.unboundid.util.args;

import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class IPAddressArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = -3923873375428600467L;
    private final boolean acceptIPv4Addresses;
    private final boolean acceptIPv6Addresses;
    
    public IPAddressArgumentValueValidator() {
        this(true, true);
    }
    
    public IPAddressArgumentValueValidator(final boolean acceptIPv4Addresses, final boolean acceptIPv6Addresses) {
        Validator.ensureTrue(acceptIPv4Addresses || acceptIPv6Addresses, "One or both of the acceptIPv4Addresses and acceptIPv6Addresses arguments must have a value of 'true'.");
        this.acceptIPv4Addresses = acceptIPv4Addresses;
        this.acceptIPv6Addresses = acceptIPv6Addresses;
    }
    
    public boolean acceptIPv4Addresses() {
        return this.acceptIPv4Addresses;
    }
    
    public boolean acceptIPv6Addresses() {
        return this.acceptIPv6Addresses;
    }
    
    @Override
    public void validateArgumentValue(final Argument argument, final String valueString) throws ArgumentException {
        final boolean isIPv6 = valueString.indexOf(58) >= 0;
        if (isIPv6) {
            for (final char c : valueString.toCharArray()) {
                if (c != ':' && c != '.' && (c < '0' || c > '9') && (c < 'a' || c > 'f') && (c < 'A' || c > 'F')) {
                    throw new ArgumentException(ArgsMessages.ERR_IP_VALIDATOR_ILLEGAL_IPV6_CHAR.get(valueString, argument.getIdentifierString(), c));
                }
            }
        }
        else {
            if (valueString.indexOf(46) < 0) {
                throw new ArgumentException(ArgsMessages.ERR_IP_VALIDATOR_MALFORMED.get(valueString, argument.getIdentifierString()));
            }
            for (final char c : valueString.toCharArray()) {
                if (c != '.' && (c < '0' || c > '9')) {
                    throw new ArgumentException(ArgsMessages.ERR_IP_VALIDATOR_ILLEGAL_IPV4_CHAR.get(valueString, argument.getIdentifierString(), c));
                }
            }
        }
        try {
            LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(valueString);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ArgumentException(ArgsMessages.ERR_IP_VALIDATOR_MALFORMED.get(valueString, argument.getIdentifierString()), e);
        }
        if (isIPv6) {
            if (!this.acceptIPv6Addresses) {
                throw new ArgumentException(ArgsMessages.ERR_IP_VALIDATOR_IPV6_NOT_ACCEPTED.get(valueString, argument.getIdentifierString()));
            }
        }
        else if (!this.acceptIPv4Addresses) {
            throw new ArgumentException(ArgsMessages.ERR_IP_VALIDATOR_IPV4_NOT_ACCEPTED.get(valueString, argument.getIdentifierString()));
        }
    }
    
    public static boolean isValidNumericIPAddress(final String s) {
        return isValidNumericIPv4Address(s) || isValidNumericIPv6Address(s);
    }
    
    public static boolean isValidNumericIPv4Address(final String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        for (final char c : s.toCharArray()) {
            if (c != '.' && (c < '0' || c > '9')) {
                return false;
            }
        }
        try {
            LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(s);
            return true;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return false;
        }
    }
    
    public static boolean isValidNumericIPv6Address(final String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        boolean colonFound = false;
        for (final char c : s.toCharArray()) {
            if (c == ':') {
                colonFound = true;
            }
            else if (c != '.' && (c < '0' || c > '9') && (c < 'a' || c > 'f')) {
                if (c < 'A' || c > 'F') {
                    return false;
                }
            }
        }
        if (colonFound) {
            try {
                LDAPConnectionOptions.DEFAULT_NAME_RESOLVER.getByName(s);
                return true;
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("IPAddressArgumentValueValidator(acceptIPv4Addresses=");
        buffer.append(this.acceptIPv4Addresses);
        buffer.append(", acceptIPv6Addresses=");
        buffer.append(this.acceptIPv6Addresses);
        buffer.append(')');
    }
}
