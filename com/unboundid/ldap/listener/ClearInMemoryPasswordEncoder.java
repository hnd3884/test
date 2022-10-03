package com.unboundid.ldap.listener;

import java.util.Arrays;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ClearInMemoryPasswordEncoder extends InMemoryPasswordEncoder
{
    public ClearInMemoryPasswordEncoder(final String prefix, final PasswordEncoderOutputFormatter outputFormatter) {
        super(prefix, outputFormatter);
    }
    
    @Override
    protected byte[] encodePassword(final byte[] clearPassword, final ReadOnlyEntry userEntry, final List<Modification> modifications) throws LDAPException {
        return clearPassword;
    }
    
    @Override
    protected void ensurePreEncodedPasswordAppearsValid(final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry, final List<Modification> modifications) throws LDAPException {
    }
    
    @Override
    protected boolean passwordMatches(final byte[] clearPasswordBytes, final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry) throws LDAPException {
        return Arrays.equals(clearPasswordBytes, unPrefixedUnFormattedEncodedPasswordBytes);
    }
    
    @Override
    protected byte[] extractClearPassword(final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry) throws LDAPException {
        return unPrefixedUnFormattedEncodedPasswordBytes;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("ClearInMemoryPasswordEncoder(prefix='");
        buffer.append(this.getPrefix());
        buffer.append("', outputFormatter=");
        final PasswordEncoderOutputFormatter outputFormatter = this.getOutputFormatter();
        if (outputFormatter == null) {
            buffer.append("null");
        }
        else {
            outputFormatter.toString(buffer);
        }
        buffer.append(')');
    }
}
