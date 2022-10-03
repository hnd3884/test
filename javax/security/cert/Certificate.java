package javax.security.cert;

import java.security.SignatureException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

public abstract class Certificate
{
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certificate)) {
            return false;
        }
        try {
            final byte[] encoded = this.getEncoded();
            final byte[] encoded2 = ((Certificate)o).getEncoded();
            if (encoded.length != encoded2.length) {
                return false;
            }
            for (int i = 0; i < encoded.length; ++i) {
                if (encoded[i] != encoded2[i]) {
                    return false;
                }
            }
            return true;
        }
        catch (final CertificateException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        try {
            final byte[] encoded = this.getEncoded();
            for (byte b = 1; b < encoded.length; ++b) {
                n += encoded[b] * b;
            }
            return n;
        }
        catch (final CertificateException ex) {
            return n;
        }
    }
    
    public abstract byte[] getEncoded() throws CertificateEncodingException;
    
    public abstract void verify(final PublicKey p0) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
    
    public abstract void verify(final PublicKey p0, final String p1) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException;
    
    @Override
    public abstract String toString();
    
    public abstract PublicKey getPublicKey();
}
