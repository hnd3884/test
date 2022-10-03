package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

final class CloneableDigest extends MessageDigest implements Cloneable
{
    private final MessageDigest[] digests;
    
    private CloneableDigest(final MessageDigest digest, final int n, final String algorithm) throws NoSuchAlgorithmException {
        super(algorithm);
        (this.digests = new MessageDigest[n])[0] = digest;
        for (int i = 1; i < n; ++i) {
            this.digests[i] = JsseJce.getMessageDigest(algorithm);
        }
    }
    
    static MessageDigest getDigest(final String algorithm, final int n) throws NoSuchAlgorithmException {
        final MessageDigest digest = JsseJce.getMessageDigest(algorithm);
        try {
            digest.clone();
            return digest;
        }
        catch (final CloneNotSupportedException e) {
            return new CloneableDigest(digest, n, algorithm);
        }
    }
    
    private void checkState() {
    }
    
    @Override
    protected int engineGetDigestLength() {
        this.checkState();
        return this.digests[0].getDigestLength();
    }
    
    @Override
    protected void engineUpdate(final byte b) {
        this.checkState();
        for (int i = 0; i < this.digests.length && this.digests[i] != null; ++i) {
            this.digests[i].update(b);
        }
    }
    
    @Override
    protected void engineUpdate(final byte[] b, final int offset, final int len) {
        this.checkState();
        for (int i = 0; i < this.digests.length && this.digests[i] != null; ++i) {
            this.digests[i].update(b, offset, len);
        }
    }
    
    @Override
    protected byte[] engineDigest() {
        this.checkState();
        final byte[] digest = this.digests[0].digest();
        this.digestReset();
        return digest;
    }
    
    @Override
    protected int engineDigest(final byte[] buf, final int offset, final int len) throws DigestException {
        this.checkState();
        final int n = this.digests[0].digest(buf, offset, len);
        this.digestReset();
        return n;
    }
    
    private void digestReset() {
        for (int i = 1; i < this.digests.length && this.digests[i] != null; ++i) {
            this.digests[i].reset();
        }
    }
    
    @Override
    protected void engineReset() {
        this.checkState();
        for (int i = 0; i < this.digests.length && this.digests[i] != null; ++i) {
            this.digests[i].reset();
        }
    }
    
    @Override
    public Object clone() {
        this.checkState();
        for (int i = this.digests.length - 1; i >= 0; --i) {
            if (this.digests[i] != null) {
                final MessageDigest digest = this.digests[i];
                this.digests[i] = null;
                return digest;
            }
        }
        throw new InternalError();
    }
}
