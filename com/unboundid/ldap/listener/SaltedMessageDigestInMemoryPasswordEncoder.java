package com.unboundid.ldap.listener;

import java.util.Arrays;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.util.Validator;
import java.security.SecureRandom;
import java.security.MessageDigest;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SaltedMessageDigestInMemoryPasswordEncoder extends InMemoryPasswordEncoder
{
    private final boolean saltAfterClearPassword;
    private final boolean saltAfterMessageDigest;
    private final int digestLengthBytes;
    private final int numSaltBytes;
    private final MessageDigest messageDigest;
    private final SecureRandom random;
    
    public SaltedMessageDigestInMemoryPasswordEncoder(final String prefix, final PasswordEncoderOutputFormatter outputFormatter, final MessageDigest messageDigest, final int numSaltBytes, final boolean saltAfterClearPassword, final boolean saltAfterMessageDigest) {
        super(prefix, outputFormatter);
        Validator.ensureNotNull(messageDigest);
        this.messageDigest = messageDigest;
        this.digestLengthBytes = messageDigest.getDigestLength();
        Validator.ensureTrue(this.digestLengthBytes > 0, "The message digest use a fixed digest length, and that length must be greater than zero.");
        this.numSaltBytes = numSaltBytes;
        Validator.ensureTrue(numSaltBytes > 0, "numSaltBytes must be greater than zero.");
        this.saltAfterClearPassword = saltAfterClearPassword;
        this.saltAfterMessageDigest = saltAfterMessageDigest;
        this.random = new SecureRandom();
    }
    
    public String getDigestAlgorithm() {
        return this.messageDigest.getAlgorithm();
    }
    
    public int getDigestLengthBytes() {
        return this.digestLengthBytes;
    }
    
    public int getNumSaltBytes() {
        return this.numSaltBytes;
    }
    
    public boolean isSaltAfterClearPassword() {
        return this.saltAfterClearPassword;
    }
    
    public boolean isSaltAfterMessageDigest() {
        return this.saltAfterMessageDigest;
    }
    
    @Override
    protected byte[] encodePassword(final byte[] clearPassword, final ReadOnlyEntry userEntry, final List<Modification> modifications) throws LDAPException {
        final byte[] salt = new byte[this.numSaltBytes];
        this.random.nextBytes(salt);
        byte[] saltedPassword;
        if (this.saltAfterClearPassword) {
            saltedPassword = concatenate(clearPassword, salt);
        }
        else {
            saltedPassword = concatenate(salt, clearPassword);
        }
        final byte[] digest = this.messageDigest.digest(saltedPassword);
        if (this.saltAfterMessageDigest) {
            return concatenate(digest, salt);
        }
        return concatenate(salt, digest);
    }
    
    private static byte[] concatenate(final byte[] b1, final byte[] b2) {
        final byte[] combined = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, combined, 0, b1.length);
        System.arraycopy(b2, 0, combined, b1.length, b2.length);
        return combined;
    }
    
    @Override
    protected void ensurePreEncodedPasswordAppearsValid(final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry, final List<Modification> modifications) throws LDAPException {
        if (unPrefixedUnFormattedEncodedPasswordBytes.length <= this.digestLengthBytes) {
            throw new LDAPException(ResultCode.PARAM_ERROR, ListenerMessages.ERR_SALTED_DIGEST_PW_ENCODER_PRE_ENCODED_LENGTH_MISMATCH.get(this.messageDigest.getAlgorithm(), unPrefixedUnFormattedEncodedPasswordBytes.length, this.digestLengthBytes + 1));
        }
    }
    
    @Override
    protected boolean passwordMatches(final byte[] clearPasswordBytes, final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry) throws LDAPException {
        final int numComputedSaltBytes = unPrefixedUnFormattedEncodedPasswordBytes.length - this.digestLengthBytes;
        if (numComputedSaltBytes <= 0) {
            return false;
        }
        final byte[] salt = new byte[numComputedSaltBytes];
        final byte[] digest = new byte[this.digestLengthBytes];
        if (this.saltAfterMessageDigest) {
            System.arraycopy(unPrefixedUnFormattedEncodedPasswordBytes, 0, digest, 0, this.digestLengthBytes);
            System.arraycopy(unPrefixedUnFormattedEncodedPasswordBytes, this.digestLengthBytes, salt, 0, salt.length);
        }
        else {
            System.arraycopy(unPrefixedUnFormattedEncodedPasswordBytes, 0, salt, 0, salt.length);
            System.arraycopy(unPrefixedUnFormattedEncodedPasswordBytes, salt.length, digest, 0, this.digestLengthBytes);
        }
        byte[] saltedPassword;
        if (this.saltAfterClearPassword) {
            saltedPassword = concatenate(clearPasswordBytes, salt);
        }
        else {
            saltedPassword = concatenate(salt, clearPasswordBytes);
        }
        final byte[] computedDigest = this.messageDigest.digest(saltedPassword);
        return Arrays.equals(computedDigest, digest);
    }
    
    @Override
    protected byte[] extractClearPassword(final byte[] unPrefixedUnFormattedEncodedPasswordBytes, final ReadOnlyEntry userEntry) throws LDAPException {
        throw new LDAPException(ResultCode.NOT_SUPPORTED, ListenerMessages.ERR_SALTED_DIGEST_PW_ENCODER_NOT_REVERSIBLE.get());
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
        buffer.append(", numSaltBytes=");
        buffer.append(this.numSaltBytes);
        buffer.append(", saltAfterClearPassword=");
        buffer.append(this.saltAfterClearPassword);
        buffer.append(", saltAfterMessageDigest=");
        buffer.append(this.saltAfterMessageDigest);
        buffer.append(')');
    }
}
