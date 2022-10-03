package org.openjsse.sun.security.ssl;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLHandshakeException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;

final class SSLSecretDerivation implements SSLKeyDerivation
{
    private static final byte[] sha256EmptyDigest;
    private static final byte[] sha384EmptyDigest;
    private final HandshakeContext context;
    private final String hkdfAlg;
    private final CipherSuite.HashAlg hashAlg;
    private final SecretKey secret;
    private final byte[] transcriptHash;
    
    SSLSecretDerivation(final HandshakeContext context, final SecretKey secret) {
        this.context = context;
        this.secret = secret;
        this.hashAlg = context.negotiatedCipherSuite.hashAlg;
        this.hkdfAlg = "HKDF-Expand/Hmac" + this.hashAlg.name.replace("-", "");
        context.handshakeHash.update();
        this.transcriptHash = context.handshakeHash.digest();
    }
    
    SSLSecretDerivation forContext(final HandshakeContext context) {
        return new SSLSecretDerivation(context, this.secret);
    }
    
    @Override
    public SecretKey deriveKey(final String algorithm, final AlgorithmParameterSpec params) throws IOException {
        final SecretSchedule ks = SecretSchedule.valueOf(algorithm);
        try {
            byte[] expandContext;
            if (ks == SecretSchedule.TlsSaltSecret) {
                if (this.hashAlg == CipherSuite.HashAlg.H_SHA256) {
                    expandContext = SSLSecretDerivation.sha256EmptyDigest;
                }
                else {
                    if (this.hashAlg != CipherSuite.HashAlg.H_SHA384) {
                        throw new SSLHandshakeException("Unexpected unsupported hash algorithm: " + algorithm);
                    }
                    expandContext = SSLSecretDerivation.sha384EmptyDigest;
                }
            }
            else {
                expandContext = this.transcriptHash;
            }
            final byte[] hkdfInfo = createHkdfInfo(ks.label, expandContext, this.hashAlg.hashLength);
            final HKDF hkdf = new HKDF(this.hashAlg.name);
            return hkdf.expand(this.secret, hkdfInfo, this.hashAlg.hashLength, algorithm);
        }
        catch (final GeneralSecurityException gse) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(gse);
        }
    }
    
    public static byte[] createHkdfInfo(final byte[] label, final byte[] context, final int length) {
        final byte[] info = new byte[4 + label.length + context.length];
        final ByteBuffer m = ByteBuffer.wrap(info);
        try {
            Record.putInt16(m, length);
            Record.putBytes8(m, label);
            Record.putBytes8(m, context);
        }
        catch (final IOException ioe) {
            throw new RuntimeException("Unexpected exception", ioe);
        }
        return info;
    }
    
    static {
        sha256EmptyDigest = new byte[] { -29, -80, -60, 66, -104, -4, 28, 20, -102, -5, -12, -56, -103, 111, -71, 36, 39, -82, 65, -28, 100, -101, -109, 76, -92, -107, -103, 27, 120, 82, -72, 85 };
        sha384EmptyDigest = new byte[] { 56, -80, 96, -89, 81, -84, -106, 56, 76, -39, 50, 126, -79, -79, -29, 106, 33, -3, -73, 17, 20, -66, 7, 67, 76, 12, -57, -65, 99, -10, -31, -38, 39, 78, -34, -65, -25, 111, 101, -5, -43, 26, -46, -15, 72, -104, -71, 91 };
    }
    
    private enum SecretSchedule
    {
        TlsSaltSecret("derived"), 
        TlsExtBinderKey("ext binder"), 
        TlsResBinderKey("res binder"), 
        TlsClientEarlyTrafficSecret("c e traffic"), 
        TlsEarlyExporterMasterSecret("e exp master"), 
        TlsClientHandshakeTrafficSecret("c hs traffic"), 
        TlsServerHandshakeTrafficSecret("s hs traffic"), 
        TlsClientAppTrafficSecret("c ap traffic"), 
        TlsServerAppTrafficSecret("s ap traffic"), 
        TlsExporterMasterSecret("exp master"), 
        TlsResumptionMasterSecret("res master");
        
        private final byte[] label;
        
        private SecretSchedule(final String label) {
            this.label = ("tls13 " + label).getBytes();
        }
    }
}
