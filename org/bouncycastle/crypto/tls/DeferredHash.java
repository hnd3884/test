package org.bouncycastle.crypto.tls;

import java.util.Enumeration;
import org.bouncycastle.util.Shorts;
import org.bouncycastle.crypto.Digest;
import java.util.Hashtable;

class DeferredHash implements TlsHandshakeHash
{
    protected static final int BUFFERING_HASH_LIMIT = 4;
    protected TlsContext context;
    private DigestInputBuffer buf;
    private Hashtable hashes;
    private Short prfHashAlgorithm;
    
    DeferredHash() {
        this.buf = new DigestInputBuffer();
        this.hashes = new Hashtable();
        this.prfHashAlgorithm = null;
    }
    
    private DeferredHash(final Short prfHashAlgorithm, final Digest digest) {
        this.buf = null;
        this.hashes = new Hashtable();
        this.prfHashAlgorithm = prfHashAlgorithm;
        this.hashes.put(prfHashAlgorithm, digest);
    }
    
    public void init(final TlsContext context) {
        this.context = context;
    }
    
    public TlsHandshakeHash notifyPRFDetermined() {
        final int prfAlgorithm = this.context.getSecurityParameters().getPrfAlgorithm();
        if (prfAlgorithm == 0) {
            final CombinedHash combinedHash = new CombinedHash();
            combinedHash.init(this.context);
            this.buf.updateDigest(combinedHash);
            return combinedHash.notifyPRFDetermined();
        }
        this.checkTrackingHash(this.prfHashAlgorithm = Shorts.valueOf(TlsUtils.getHashAlgorithmForPRFAlgorithm(prfAlgorithm)));
        return this;
    }
    
    public void trackHashAlgorithm(final short n) {
        if (this.buf == null) {
            throw new IllegalStateException("Too late to track more hash algorithms");
        }
        this.checkTrackingHash(Shorts.valueOf(n));
    }
    
    public void sealHashAlgorithms() {
        this.checkStopBuffering();
    }
    
    public TlsHandshakeHash stopTracking() {
        final Digest cloneHash = TlsUtils.cloneHash(this.prfHashAlgorithm, this.hashes.get(this.prfHashAlgorithm));
        if (this.buf != null) {
            this.buf.updateDigest(cloneHash);
        }
        final DeferredHash deferredHash = new DeferredHash(this.prfHashAlgorithm, cloneHash);
        deferredHash.init(this.context);
        return deferredHash;
    }
    
    public Digest forkPRFHash() {
        this.checkStopBuffering();
        if (this.buf != null) {
            final Digest hash = TlsUtils.createHash(this.prfHashAlgorithm);
            this.buf.updateDigest(hash);
            return hash;
        }
        return TlsUtils.cloneHash(this.prfHashAlgorithm, this.hashes.get(this.prfHashAlgorithm));
    }
    
    public byte[] getFinalHash(final short n) {
        final Digest digest = this.hashes.get(Shorts.valueOf(n));
        if (digest == null) {
            throw new IllegalStateException("HashAlgorithm." + HashAlgorithm.getText(n) + " is not being tracked");
        }
        final Digest cloneHash = TlsUtils.cloneHash(n, digest);
        if (this.buf != null) {
            this.buf.updateDigest(cloneHash);
        }
        final byte[] array = new byte[cloneHash.getDigestSize()];
        cloneHash.doFinal(array, 0);
        return array;
    }
    
    public String getAlgorithmName() {
        throw new IllegalStateException("Use fork() to get a definite Digest");
    }
    
    public int getDigestSize() {
        throw new IllegalStateException("Use fork() to get a definite Digest");
    }
    
    public void update(final byte b) {
        if (this.buf != null) {
            this.buf.write(b);
            return;
        }
        final Enumeration elements = this.hashes.elements();
        while (elements.hasMoreElements()) {
            ((Digest)elements.nextElement()).update(b);
        }
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        if (this.buf != null) {
            this.buf.write(array, n, n2);
            return;
        }
        final Enumeration elements = this.hashes.elements();
        while (elements.hasMoreElements()) {
            ((Digest)elements.nextElement()).update(array, n, n2);
        }
    }
    
    public int doFinal(final byte[] array, final int n) {
        throw new IllegalStateException("Use fork() to get a definite Digest");
    }
    
    public void reset() {
        if (this.buf != null) {
            this.buf.reset();
            return;
        }
        final Enumeration elements = this.hashes.elements();
        while (elements.hasMoreElements()) {
            ((Digest)elements.nextElement()).reset();
        }
    }
    
    protected void checkStopBuffering() {
        if (this.buf != null && this.hashes.size() <= 4) {
            final Enumeration elements = this.hashes.elements();
            while (elements.hasMoreElements()) {
                this.buf.updateDigest((Digest)elements.nextElement());
            }
            this.buf = null;
        }
    }
    
    protected void checkTrackingHash(final Short n) {
        if (!this.hashes.containsKey(n)) {
            this.hashes.put(n, TlsUtils.createHash(n));
        }
    }
}
