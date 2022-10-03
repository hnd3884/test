package com.unboundid.ldap.listener;

import java.util.Arrays;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.util.Validator;
import java.security.MessageDigest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class UnsaltedMessageDigestInMemoryPasswordEncoder extends InMemoryPasswordEncoder
{
    private final int digestLengthBytes;
    private final MessageDigest messageDigest;
    
    public UnsaltedMessageDigestInMemoryPasswordEncoder(final String prefix, final PasswordEncoderOutputFormatter outputFormatter, final MessageDigest messageDigest) {
        super(prefix, outputFormatter);
        Validator.ensureNotNull(messageDigest);
        this.messageDigest = messageDigest;
        this.digestLengthBytes = messageDigest.getDigestLength();
        Validator.ensureTrue(this.digestLengthBytes > 0, "The message digest use a fixed digest length, and that length must be greater than zero.");
    }
    
    public String getDigestAlgorithm() {
        return this.messageDigest.getAlgorithm();
    }
    
    public int getDigestLengthBytes() {
        return this.digestLengthBytes;
    }
    
    @Override
    protected byte[] encodePassword(final byte[] clearPassword, final ReadOnlyEntry userEntry, final List<Modification> modifications) throws LDAPException {
        return this.messageDigest.digest(clearPassword);
    }
    
    @Override
    protected void ensurePreEncodedPasswordAppearsValid(final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry, final List<Modification> modifications) throws LDAPException {
        if (unPrefixedUnFormattedEncodedPasswordBytes.length != this.digestLengthBytes) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_UNSALTED_DIGEST_PW_ENCODER_PRE_ENCODED_LENGTH_MISMATCH.get(this.messageDigest.getAlgorithm(), unPrefixedUnFormattedEncodedPasswordBytes.length, this.digestLengthBytes));
        }
    }
    
    @Override
    protected boolean passwordMatches(final byte[] clearPasswordBytes, final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry) throws LDAPException {
        final byte[] expectedEncodedPassword = this.messageDigest.digest(clearPasswordBytes);
        return Arrays.equals(unPrefixedUnFormattedEncodedPasswordBytes, expectedEncodedPassword);
    }
    
    @Override
    protected byte[] extractClearPassword(final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry) throws LDAPException {
        throw new LDAPException(ResultCode.NOT_SUPPORTED, ListenerMessages.ERR_UNSALTED_DIGEST_PW_ENCODER_NOT_REVERSIBLE.get());
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SaltedMessageDigestInMemoryPasswordEncoder(prefix='");
        buffer.append(this.getPrefix());
        buffer.append("', outputFormatter=");
        final PasswordEncoderOutputFormatter outputFormatter = this.getOutputFormatter();
        if (outputFormatter == null) {
            buffer.append("null");
        }
        else {
            outputFormatter.toString(buffer);
        }
        buffer.append(", digestAlgorithm='");
        buffer.append(this.messageDigest.getAlgorithm());
        buffer.append("', digestLengthBytes=");
        buffer.append(this.messageDigest.getDigestLength());
        buffer.append(')');
    }
}
