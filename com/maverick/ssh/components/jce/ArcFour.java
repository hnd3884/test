package com.maverick.ssh.components.jce;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import java.io.IOException;

public class ArcFour extends AbstractJCECipher
{
    public ArcFour() throws IOException {
        super("ARCFOUR", "ARCFOUR", 16, "arcfour");
    }
    
    public void init(final int n, final byte[] array, final byte[] array2) throws IOException {
        try {
            super.u = ((JCEProvider.getProviderForAlgorithm(super.v) == null) ? Cipher.getInstance(super.v) : Cipher.getInstance(super.v, JCEProvider.getProviderForAlgorithm(super.v)));
            if (super.u == null) {
                throw new IOException("Failed to create cipher engine for " + super.v);
            }
            final byte[] array3 = new byte[super.w];
            System.arraycopy(array2, 0, array3, 0, array3.length);
            super.u.init((n == 0) ? 1 : 2, new SecretKeySpec(array3, super.x));
        }
        catch (final NoSuchPaddingException ex) {
            throw new IOException("Padding type not supported");
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new IOException("Algorithm not supported:" + super.v);
        }
        catch (final InvalidKeyException ex3) {
            throw new IOException("Invalid encryption key");
        }
    }
    
    public int getBlockSize() {
        return 8;
    }
}
