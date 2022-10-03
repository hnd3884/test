package sun.security.ssl;

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
    
    SSLBasicKeyDerivation(final SecretKey secret, final String s, final byte[] array, final byte[] array2, final int n) {
        this.hashAlg = s.replace("-", "");
        this.secret = secret;
        this.hkdfInfo = createHkdfInfo(array, array2, n);
    }
    
    @Override
    public SecretKey deriveKey(final String s, final AlgorithmParameterSpec algorithmParameterSpec) throws IOException {
        try {
            return new HKDF(this.hashAlg).expand(this.secret, this.hkdfInfo, ((SecretSizeSpec)algorithmParameterSpec).length, s);
        }
        catch (final GeneralSecurityException ex) {
            throw (SSLHandshakeException)new SSLHandshakeException("Could not generate secret").initCause(ex);
        }
    }
    
    private static byte[] createHkdfInfo(final byte[] array, final byte[] array2, final int n) {
        final byte[] array3 = new byte[4 + array.length + array2.length];
        final ByteBuffer wrap = ByteBuffer.wrap(array3);
        try {
            Record.putInt16(wrap, n);
            Record.putBytes8(wrap, array);
            Record.putBytes8(wrap, array2);
        }
        catch (final IOException ex) {}
        return array3;
    }
    
    static class SecretSizeSpec implements AlgorithmParameterSpec
    {
        final int length;
        
        SecretSizeSpec(final int length) {
            this.length = length;
        }
    }
}
