package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Digest;

class CombinedHash implements TlsHandshakeHash
{
    protected TlsContext context;
    protected Digest md5;
    protected Digest sha1;
    
    CombinedHash() {
        this.md5 = TlsUtils.createHash((short)1);
        this.sha1 = TlsUtils.createHash((short)2);
    }
    
    CombinedHash(final CombinedHash combinedHash) {
        this.context = combinedHash.context;
        this.md5 = TlsUtils.cloneHash((short)1, combinedHash.md5);
        this.sha1 = TlsUtils.cloneHash((short)2, combinedHash.sha1);
    }
    
    public void init(final TlsContext context) {
        this.context = context;
    }
    
    public TlsHandshakeHash notifyPRFDetermined() {
        return this;
    }
    
    public void trackHashAlgorithm(final short n) {
        throw new IllegalStateException("CombinedHash only supports calculating the legacy PRF for handshake hash");
    }
    
    public void sealHashAlgorithms() {
    }
    
    public TlsHandshakeHash stopTracking() {
        return new CombinedHash(this);
    }
    
    public Digest forkPRFHash() {
        return new CombinedHash(this);
    }
    
    public byte[] getFinalHash(final short n) {
        throw new IllegalStateException("CombinedHash doesn't support multiple hashes");
    }
    
    public String getAlgorithmName() {
        return this.md5.getAlgorithmName() + " and " + this.sha1.getAlgorithmName();
    }
    
    public int getDigestSize() {
        return this.md5.getDigestSize() + this.sha1.getDigestSize();
    }
    
    public void update(final byte b) {
        this.md5.update(b);
        this.sha1.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.md5.update(array, n, n2);
        this.sha1.update(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) {
        if (this.context != null && TlsUtils.isSSL(this.context)) {
            this.ssl3Complete(this.md5, SSL3Mac.IPAD, SSL3Mac.OPAD, 48);
            this.ssl3Complete(this.sha1, SSL3Mac.IPAD, SSL3Mac.OPAD, 40);
        }
        final int doFinal = this.md5.doFinal(array, n);
        return doFinal + this.sha1.doFinal(array, n + doFinal);
    }
    
    public void reset() {
        this.md5.reset();
        this.sha1.reset();
    }
    
    protected void ssl3Complete(final Digest digest, final byte[] array, final byte[] array2, final int n) {
        final byte[] masterSecret = this.context.getSecurityParameters().masterSecret;
        digest.update(masterSecret, 0, masterSecret.length);
        digest.update(array, 0, n);
        final byte[] array3 = new byte[digest.getDigestSize()];
        digest.doFinal(array3, 0);
        digest.update(masterSecret, 0, masterSecret.length);
        digest.update(array2, 0, n);
        digest.update(array3, 0, array3.length);
    }
}
