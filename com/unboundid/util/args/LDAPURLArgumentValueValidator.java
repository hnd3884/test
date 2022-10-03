package com.unboundid.util.args;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPURL;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPURLArgumentValueValidator extends ArgumentValueValidator implements Serializable
{
    private static final long serialVersionUID = -8867023666922488786L;
    private final boolean requireAttributes;
    private final boolean requireBaseDN;
    private final boolean requireFilter;
    private final boolean requireHost;
    private final boolean requirePort;
    private final boolean requireScope;
    
    public LDAPURLArgumentValueValidator() {
        this(false, false, false, false, false, false);
    }
    
    public LDAPURLArgumentValueValidator(final boolean requireHost, final boolean requirePort, final boolean requireBaseDN, final boolean requireAttributes, final boolean requireScope, final boolean requireFilter) {
        this.requireHost = requireHost;
        this.requirePort = requirePort;
        this.requireBaseDN = requireBaseDN;
        this.requireAttributes = requireAttributes;
        this.requireScope = requireScope;
        this.requireFilter = requireFilter;
    }
    
    public boolean requireHost() {
        return this.requireHost;
    }
    
    public boolean requirePort() {
        return this.requirePort;
    }
    
    public boolean requireBaseDN() {
        return this.requireBaseDN;
    }
    
    public boolean requireAttributes() {
        return this.requireAttributes;
    }
    
    public boolean requireScope() {
        return this.requireScope;
    }
    
    public boolean requireFilter() {
        return this.requireFilter;
    }
    
    @Override
    public void validateArgumentValue(final Argument argument, final String valueString) throws ArgumentException {
        LDAPURL ldapURL;
        try {
            ldapURL = new LDAPURL(valueString);
        }
        catch (final LDAPException e) {
            Debug.debugException(e);
            throw new ArgumentException(ArgsMessages.ERR_LDAP_URL_VALIDATOR_VALUE_NOT_LDAP_URL.get(valueString, argument.getIdentifierString(), e.getMessage()), e);
        }
        if (this.requireHost && !ldapURL.hostProvided()) {
            throw new ArgumentException(ArgsMessages.ERR_LDAP_URL_VALIDATOR_MISSING_HOST.get(valueString, argument.getIdentifierString()));
        }
        if (this.requirePort && !ldapURL.portProvided()) {
            throw new ArgumentException(ArgsMessages.ERR_LDAP_URL_VALIDATOR_MISSING_PORT.get(valueString, argument.getIdentifierString()));
        }
        if (this.requireBaseDN && !ldapURL.baseDNProvided()) {
            throw new ArgumentException(ArgsMessages.ERR_LDAP_URL_VALIDATOR_MISSING_BASE_DN.get(valueString, argument.getIdentifierString()));
        }
        if (this.requireAttributes && !ldapURL.attributesProvided()) {
            throw new ArgumentException(ArgsMessages.ERR_LDAP_URL_VALIDATOR_MISSING_ATTRIBUTES.get(valueString, argument.getIdentifierString()));
        }
        if (this.requireScope && !ldapURL.scopeProvided()) {
            throw new ArgumentException(ArgsMessages.ERR_LDAP_URL_VALIDATOR_MISSING_SCOPE.get(valueString, argument.getIdentifierString()));
        }
        if (this.requireFilter && !ldapURL.filterProvided()) {
            throw new ArgumentException(ArgsMessages.ERR_LDAP_URL_VALIDATOR_MISSING_FILTER.get(valueString, argument.getIdentifierString()));
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPURLArgumentValueValidator(requireHost=");
        buffer.append(this.requireHost);
        buffer.append(", requirePort=");
        buffer.append(this.requirePort);
        buffer.append(", requireBaseDN=");
        buffer.append(this.requireBaseDN);
        buffer.append(", requireAttributes=");
        buffer.append(this.requireAttributes);
        buffer.append(", requireScope=");
        buffer.append(this.requireScope);
        buffer.append(", requireFilter=");
        buffer.append(this.requireFilter);
        buffer.append(')');
    }
}
