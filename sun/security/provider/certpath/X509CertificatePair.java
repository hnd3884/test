package sun.security.provider.certpath;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import java.security.GeneralSecurityException;
import java.security.interfaces.DSAPublicKey;
import sun.security.provider.X509Factory;
import sun.security.x509.X509CertImpl;
import java.security.cert.CertificateEncodingException;
import sun.security.util.DerOutputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import java.security.cert.CertificateException;
import sun.security.util.Cache;
import java.security.cert.X509Certificate;

public class X509CertificatePair
{
    private static final byte TAG_FORWARD = 0;
    private static final byte TAG_REVERSE = 1;
    private X509Certificate forward;
    private X509Certificate reverse;
    private byte[] encoded;
    private static final Cache<Object, X509CertificatePair> cache;
    
    public X509CertificatePair() {
    }
    
    public X509CertificatePair(final X509Certificate forward, final X509Certificate reverse) throws CertificateException {
        if (forward == null && reverse == null) {
            throw new CertificateException("at least one of certificate pair must be non-null");
        }
        this.forward = forward;
        this.reverse = reverse;
        this.checkPair();
    }
    
    private X509CertificatePair(final byte[] encoded) throws CertificateException {
        try {
            this.parse(new DerValue(encoded));
            this.encoded = encoded;
        }
        catch (final IOException ex) {
            throw new CertificateException(ex.toString());
        }
        this.checkPair();
    }
    
    public static synchronized void clearCache() {
        X509CertificatePair.cache.clear();
    }
    
    public static synchronized X509CertificatePair generateCertificatePair(final byte[] array) throws CertificateException {
        final X509CertificatePair x509CertificatePair = X509CertificatePair.cache.get(new Cache.EqualByteArray(array));
        if (x509CertificatePair != null) {
            return x509CertificatePair;
        }
        final X509CertificatePair x509CertificatePair2 = new X509CertificatePair(array);
        X509CertificatePair.cache.put(new Cache.EqualByteArray(x509CertificatePair2.encoded), x509CertificatePair2);
        return x509CertificatePair2;
    }
    
    public void setForward(final X509Certificate forward) throws CertificateException {
        this.checkPair();
        this.forward = forward;
    }
    
    public void setReverse(final X509Certificate reverse) throws CertificateException {
        this.checkPair();
        this.reverse = reverse;
    }
    
    public X509Certificate getForward() {
        return this.forward;
    }
    
    public X509Certificate getReverse() {
        return this.reverse;
    }
    
    public byte[] getEncoded() throws CertificateEncodingException {
        try {
            if (this.encoded == null) {
                final DerOutputStream derOutputStream = new DerOutputStream();
                this.emit(derOutputStream);
                this.encoded = derOutputStream.toByteArray();
            }
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.toString());
        }
        return this.encoded;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("X.509 Certificate Pair: [\n");
        if (this.forward != null) {
            sb.append("  Forward: ").append(this.forward).append("\n");
        }
        if (this.reverse != null) {
            sb.append("  Reverse: ").append(this.reverse).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
    
    private void parse(final DerValue derValue) throws IOException, CertificateException {
        if (derValue.tag != 48) {
            throw new IOException("Sequence tag missing for X509CertificatePair");
        }
        while (derValue.data != null && derValue.data.available() != 0) {
            final DerValue derValue2 = derValue.data.getDerValue();
            switch ((short)(byte)(derValue2.tag & 0x1F)) {
                case 0: {
                    if (!derValue2.isContextSpecific() || !derValue2.isConstructed()) {
                        continue;
                    }
                    if (this.forward != null) {
                        throw new IOException("Duplicate forward certificate in X509CertificatePair");
                    }
                    this.forward = X509Factory.intern(new X509CertImpl(derValue2.data.getDerValue().toByteArray()));
                    continue;
                }
                case 1: {
                    if (!derValue2.isContextSpecific() || !derValue2.isConstructed()) {
                        continue;
                    }
                    if (this.reverse != null) {
                        throw new IOException("Duplicate reverse certificate in X509CertificatePair");
                    }
                    this.reverse = X509Factory.intern(new X509CertImpl(derValue2.data.getDerValue().toByteArray()));
                    continue;
                }
                default: {
                    throw new IOException("Invalid encoding of X509CertificatePair");
                }
            }
        }
        if (this.forward == null && this.reverse == null) {
            throw new CertificateException("at least one of certificate pair must be non-null");
        }
    }
    
    private void emit(final DerOutputStream derOutputStream) throws IOException, CertificateEncodingException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        if (this.forward != null) {
            final DerOutputStream derOutputStream3 = new DerOutputStream();
            derOutputStream3.putDerValue(new DerValue(this.forward.getEncoded()));
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)0), derOutputStream3);
        }
        if (this.reverse != null) {
            final DerOutputStream derOutputStream4 = new DerOutputStream();
            derOutputStream4.putDerValue(new DerValue(this.reverse.getEncoded()));
            derOutputStream2.write(DerValue.createTag((byte)(-128), true, (byte)1), derOutputStream4);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    private void checkPair() throws CertificateException {
        if (this.forward == null || this.reverse == null) {
            return;
        }
        final X500Principal subjectX500Principal = this.forward.getSubjectX500Principal();
        final X500Principal issuerX500Principal = this.forward.getIssuerX500Principal();
        final X500Principal subjectX500Principal2 = this.reverse.getSubjectX500Principal();
        final X500Principal issuerX500Principal2 = this.reverse.getIssuerX500Principal();
        if (!issuerX500Principal.equals(subjectX500Principal2) || !issuerX500Principal2.equals(subjectX500Principal)) {
            throw new CertificateException("subject and issuer names in forward and reverse certificates do not match");
        }
        try {
            final PublicKey publicKey = this.reverse.getPublicKey();
            if (!(publicKey instanceof DSAPublicKey) || ((DSAPublicKey)publicKey).getParams() != null) {
                this.forward.verify(publicKey);
            }
            final PublicKey publicKey2 = this.forward.getPublicKey();
            if (!(publicKey2 instanceof DSAPublicKey) || ((DSAPublicKey)publicKey2).getParams() != null) {
                this.reverse.verify(publicKey2);
            }
        }
        catch (final GeneralSecurityException ex) {
            throw new CertificateException("invalid signature: " + ex.getMessage());
        }
    }
    
    static {
        cache = Cache.newSoftMemoryCache(750);
    }
}
