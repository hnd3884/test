package org.bouncycastle.crypto.tls;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import org.bouncycastle.util.Arrays;

public final class SessionParameters
{
    private int cipherSuite;
    private short compressionAlgorithm;
    private byte[] masterSecret;
    private Certificate peerCertificate;
    private byte[] pskIdentity;
    private byte[] srpIdentity;
    private byte[] encodedServerExtensions;
    
    private SessionParameters(final int cipherSuite, final short compressionAlgorithm, final byte[] array, final Certificate peerCertificate, final byte[] array2, final byte[] array3, final byte[] encodedServerExtensions) {
        this.pskIdentity = null;
        this.srpIdentity = null;
        this.cipherSuite = cipherSuite;
        this.compressionAlgorithm = compressionAlgorithm;
        this.masterSecret = Arrays.clone(array);
        this.peerCertificate = peerCertificate;
        this.pskIdentity = Arrays.clone(array2);
        this.srpIdentity = Arrays.clone(array3);
        this.encodedServerExtensions = encodedServerExtensions;
    }
    
    public void clear() {
        if (this.masterSecret != null) {
            Arrays.fill(this.masterSecret, (byte)0);
        }
    }
    
    public SessionParameters copy() {
        return new SessionParameters(this.cipherSuite, this.compressionAlgorithm, this.masterSecret, this.peerCertificate, this.pskIdentity, this.srpIdentity, this.encodedServerExtensions);
    }
    
    public int getCipherSuite() {
        return this.cipherSuite;
    }
    
    public short getCompressionAlgorithm() {
        return this.compressionAlgorithm;
    }
    
    public byte[] getMasterSecret() {
        return this.masterSecret;
    }
    
    public Certificate getPeerCertificate() {
        return this.peerCertificate;
    }
    
    @Deprecated
    public byte[] getPskIdentity() {
        return this.pskIdentity;
    }
    
    public byte[] getPSKIdentity() {
        return this.pskIdentity;
    }
    
    public byte[] getSRPIdentity() {
        return this.srpIdentity;
    }
    
    public Hashtable readServerExtensions() throws IOException {
        if (this.encodedServerExtensions == null) {
            return null;
        }
        return TlsProtocol.readExtensions(new ByteArrayInputStream(this.encodedServerExtensions));
    }
    
    public static final class Builder
    {
        private int cipherSuite;
        private short compressionAlgorithm;
        private byte[] masterSecret;
        private Certificate peerCertificate;
        private byte[] pskIdentity;
        private byte[] srpIdentity;
        private byte[] encodedServerExtensions;
        
        public Builder() {
            this.cipherSuite = -1;
            this.compressionAlgorithm = -1;
            this.masterSecret = null;
            this.peerCertificate = null;
            this.pskIdentity = null;
            this.srpIdentity = null;
            this.encodedServerExtensions = null;
        }
        
        public SessionParameters build() {
            this.validate(this.cipherSuite >= 0, "cipherSuite");
            this.validate(this.compressionAlgorithm >= 0, "compressionAlgorithm");
            this.validate(this.masterSecret != null, "masterSecret");
            return new SessionParameters(this.cipherSuite, this.compressionAlgorithm, this.masterSecret, this.peerCertificate, this.pskIdentity, this.srpIdentity, this.encodedServerExtensions, null);
        }
        
        public Builder setCipherSuite(final int cipherSuite) {
            this.cipherSuite = cipherSuite;
            return this;
        }
        
        public Builder setCompressionAlgorithm(final short compressionAlgorithm) {
            this.compressionAlgorithm = compressionAlgorithm;
            return this;
        }
        
        public Builder setMasterSecret(final byte[] masterSecret) {
            this.masterSecret = masterSecret;
            return this;
        }
        
        public Builder setPeerCertificate(final Certificate peerCertificate) {
            this.peerCertificate = peerCertificate;
            return this;
        }
        
        @Deprecated
        public Builder setPskIdentity(final byte[] pskIdentity) {
            this.pskIdentity = pskIdentity;
            return this;
        }
        
        public Builder setPSKIdentity(final byte[] pskIdentity) {
            this.pskIdentity = pskIdentity;
            return this;
        }
        
        public Builder setSRPIdentity(final byte[] srpIdentity) {
            this.srpIdentity = srpIdentity;
            return this;
        }
        
        public Builder setServerExtensions(final Hashtable hashtable) throws IOException {
            if (hashtable == null) {
                this.encodedServerExtensions = null;
            }
            else {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                TlsProtocol.writeExtensions(byteArrayOutputStream, hashtable);
                this.encodedServerExtensions = byteArrayOutputStream.toByteArray();
            }
            return this;
        }
        
        private void validate(final boolean b, final String s) {
            if (!b) {
                throw new IllegalStateException("Required session parameter '" + s + "' not configured");
            }
        }
    }
}
