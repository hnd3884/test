package org.openjsse.sun.security.ssl;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLHandshakeException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;

final class SSLBasicKeyDerivation implements SSLKeyDerivation
{
    private final String hashAlg;
    private final SecretKey secret;
    private final byte[] hkdfInfo;
    
    SSLBasicKeyDerivation(final SecretKey secret, final String hashAlg, final byte[] label, final byte[] context, final int length) {
        this.hashAlg = hashAlg.replace("-", "");
        this.secret = secret;
        this.hkdfInfo = createHkdfInfo(label, context, length);
    }
    
    @Override
    public SecretKey deriveKey(final String algorithm, final AlgorithmParameterSpec keySpec) throws IOException {
        try {
            final HKDF hkdf = new HKDF(this.hashAlg);
            return hkdf.expand(this.secret, this.hkdfInfo, ((SecretSizeSpec)keySpec).length, algorithm);
        }
        catch (final GeneralSecurityException gse) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(gse);
        }
    }
    
    private static byte[] createHkdfInfo(final byte[] label, final byte[] context, final int length) {
        final byte[] info = new byte[4 + label.length + context.length];
        final ByteBuffer m = ByteBuffer.wrap(info);
        try {
            Record.putInt16(m, length);
            Record.putBytes8(m, label);
            Record.putBytes8(m, context);
        }
        catch (final IOException ex) {}
        return info;
    }
    
    static class SecretSizeSpec implements AlgorithmParameterSpec
    {
        final int length;
        
        SecretSizeSpec(final int length) {
            this.length = length;
        }
    }
}
