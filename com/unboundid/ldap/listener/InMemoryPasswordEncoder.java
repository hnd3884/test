package com.unboundid.ldap.listener;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class InMemoryPasswordEncoder
{
    private final byte[] prefixBytes;
    private final PasswordEncoderOutputFormatter outputFormatter;
    private final String prefix;
    
    protected InMemoryPasswordEncoder(final String prefix, final PasswordEncoderOutputFormatter outputFormatter) {
        Validator.ensureNotNullOrEmpty(prefix, "The password encoder prefix must not be null or empty.");
        this.prefix = prefix;
        this.outputFormatter = outputFormatter;
        this.prefixBytes = StaticUtils.getBytes(prefix);
    }
    
    public final String getPrefix() {
        return this.prefix;
    }
    
    public final PasswordEncoderOutputFormatter getOutputFormatter() {
        return this.outputFormatter;
    }
    
    public final ASN1OctetString encodePassword(final ASN1OctetString clearPassword, final ReadOnlyEntry userEntry, final List<Modification> modifications) throws LDAPException {
        if (clearPassword.getValueLength() == 0) {
            throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_PW_ENCODER_ENCODE_PASSWORD_EMPTY.get());
        }
        final byte[] clearPasswordBytes = clearPassword.getValue();
        final byte[] encodedPasswordBytes = this.encodePassword(clearPasswordBytes, userEntry, modifications);
        byte[] formattedEncodedPasswordBytes;
        if (this.outputFormatter == null) {
            formattedEncodedPasswordBytes = encodedPasswordBytes;
        }
        else {
            formattedEncodedPasswordBytes = this.outputFormatter.format(encodedPasswordBytes);
        }
        final byte[] formattedPasswordBytesWithPrefix = new byte[formattedEncodedPasswordBytes.length + this.prefixBytes.length];
        System.arraycopy(this.prefixBytes, 0, formattedPasswordBytesWithPrefix, 0, this.prefixBytes.length);
        System.arraycopy(formattedEncodedPasswordBytes, 0, formattedPasswordBytesWithPrefix, this.prefixBytes.length, formattedEncodedPasswordBytes.length);
        return new ASN1OctetString(formattedPasswordBytesWithPrefix);
    }
    
    protected abstract byte[] encodePassword(final byte[] p0, final ReadOnlyEntry p1, final List<Modification> p2) throws LDAPException;
    
    public final void ensurePreEncodedPasswordAppearsValid(final ASN1OctetString prefixedFormattedEncodedPassword, final ReadOnlyEntry userEntry, final List<Modification> modifications) throws LDAPException {
        final byte[] prefixedFormattedEncodedPasswordBytes = prefixedFormattedEncodedPassword.getValue();
        if (!this.passwordStartsWithPrefix(prefixedFormattedEncodedPasswordBytes)) {
            throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_PW_ENCODER_VALIDATE_ENCODED_PW_MISSING_PREFIX.get(this.getClass().getName(), this.prefix));
        }
        final byte[] unPrefixedFormattedEncodedPasswordBytes = new byte[prefixedFormattedEncodedPasswordBytes.length - this.prefixBytes.length];
        System.arraycopy(prefixedFormattedEncodedPasswordBytes, this.prefixBytes.length, unPrefixedFormattedEncodedPasswordBytes, 0, unPrefixedFormattedEncodedPasswordBytes.length);
        byte[] unPrefixedUnFormattedEncodedPasswordBytes;
        if (this.outputFormatter == null) {
            unPrefixedUnFormattedEncodedPasswordBytes = unPrefixedFormattedEncodedPasswordBytes;
        }
        else {
            unPrefixedUnFormattedEncodedPasswordBytes = this.outputFormatter.unFormat(unPrefixedFormattedEncodedPasswordBytes);
        }
        this.ensurePreEncodedPasswordAppearsValid(unPrefixedUnFormattedEncodedPasswordBytes, userEntry, modifications);
    }
    
    protected abstract void ensurePreEncodedPasswordAppearsValid(final byte[] p0, final ReadOnlyEntry p1, final List<Modification> p2) throws LDAPException;
    
    public final boolean clearPasswordMatchesEncodedPassword(final ASN1OctetString clearPassword, final ASN1OctetString prefixedFormattedEncodedPassword, final ReadOnlyEntry userEntry) throws LDAPException {
        final byte[] clearPasswordBytes = clearPassword.getValue();
        if (clearPasswordBytes.length == 0) {
            return false;
        }
        final byte[] prefixedFormattedEncodedPasswordBytes = prefixedFormattedEncodedPassword.getValue();
        if (!this.passwordStartsWithPrefix(prefixedFormattedEncodedPasswordBytes)) {
            return false;
        }
        final byte[] unPrefixedFormattedEncodedPasswordBytes = new byte[prefixedFormattedEncodedPasswordBytes.length - this.prefixBytes.length];
        System.arraycopy(prefixedFormattedEncodedPasswordBytes, this.prefixBytes.length, unPrefixedFormattedEncodedPasswordBytes, 0, unPrefixedFormattedEncodedPasswordBytes.length);
        byte[] unPrefixedUnFormattedEncodedPasswordBytes;
        if (this.outputFormatter == null) {
            unPrefixedUnFormattedEncodedPasswordBytes = unPrefixedFormattedEncodedPasswordBytes;
        }
        else {
            unPrefixedUnFormattedEncodedPasswordBytes = this.outputFormatter.unFormat(unPrefixedFormattedEncodedPasswordBytes);
        }
        return unPrefixedUnFormattedEncodedPasswordBytes.length != 0 && this.passwordMatches(clearPasswordBytes, unPrefixedUnFormattedEncodedPasswordBytes, userEntry);
    }
    
    protected abstract boolean passwordMatches(final byte[] p0, final byte[] p1, final ReadOnlyEntry p2) throws LDAPException;
    
    public final ASN1OctetString extractClearPasswordFromEncodedPassword(final ASN1OctetString prefixedFormattedEncodedPassword, final ReadOnlyEntry userEntry) throws LDAPException {
        final byte[] prefixedFormattedEncodedPasswordBytes = prefixedFormattedEncodedPassword.getValue();
        if (!this.passwordStartsWithPrefix(prefixedFormattedEncodedPasswordBytes)) {
            throw new LDAPException(ResultCode.UNWILLING_TO_PERFORM, ListenerMessages.ERR_PW_ENCODER_PW_MATCHES_ENCODED_PW_MISSING_PREFIX.get(this.getClass().getName(), this.prefix));
        }
        final byte[] unPrefixedFormattedEncodedPasswordBytes = new byte[prefixedFormattedEncodedPasswordBytes.length - this.prefixBytes.length];
        System.arraycopy(prefixedFormattedEncodedPasswordBytes, this.prefixBytes.length, unPrefixedFormattedEncodedPasswordBytes, 0, unPrefixedFormattedEncodedPasswordBytes.length);
        byte[] unPrefixedUnFormattedEncodedPasswordBytes;
        if (this.outputFormatter == null) {
            unPrefixedUnFormattedEncodedPasswordBytes = unPrefixedFormattedEncodedPasswordBytes;
        }
        else {
            unPrefixedUnFormattedEncodedPasswordBytes = this.outputFormatter.unFormat(unPrefixedFormattedEncodedPasswordBytes);
        }
        final byte[] clearPasswordBytes = this.extractClearPassword(unPrefixedUnFormattedEncodedPasswordBytes, userEntry);
        return new ASN1OctetString(clearPasswordBytes);
    }
    
    protected abstract byte[] extractClearPassword(final byte[] p0, final ReadOnlyEntry p1) throws LDAPException;
    
    public final boolean passwordStartsWithPrefix(final ASN1OctetString password) {
        return this.passwordStartsWithPrefix(password.getValue());
    }
    
    private boolean passwordStartsWithPrefix(final byte[] b) {
        if (b.length < this.prefixBytes.length) {
            return false;
        }
        for (int i = 0; i < this.prefixBytes.length; ++i) {
            if (b[i] != this.prefixBytes[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public abstract void toString(final StringBuilder p0);
}
