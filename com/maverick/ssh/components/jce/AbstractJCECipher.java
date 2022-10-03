package com.maverick.ssh.components.jce;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import javax.crypto.Cipher;
import com.maverick.ssh.components.SshCipher;

public class AbstractJCECipher extends SshCipher
{
    Cipher u;
    String v;
    String x;
    int w;
    
    public AbstractJCECipher(final String v, final String x, final int w, final String s) throws IOException {
        super(s);
        this.v = v;
        this.w = w;
        this.x = x;
        try {
            this.u = ((JCEProvider.getProviderForAlgorithm(v) == null) ? Cipher.getInstance(v) : Cipher.getInstance(v, JCEProvider.getProviderForAlgorithm(v)));
        }
        catch (final NoSuchPaddingException ex) {
            throw new IOException("Padding type not supported");
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new IOException("Algorithm not supported:" + v);
        }
        if (this.u == null) {
            throw new IOException("Failed to create cipher engine for " + v);
        }
    }
    
    public void transform(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
        if (n3 > 0) {
            System.arraycopy(this.u.update(array, n, n3), 0, array2, n2, n3);
        }
    }
    
    public String getProvider() {
        return this.u.getProvider().getName();
    }
    
    public void init(final int n, final byte[] array, final byte[] array2) throws IOException {
        try {
            final byte[] array3 = new byte[this.w];
            System.arraycopy(array2, 0, array3, 0, array3.length);
            this.u.init((n == 0) ? 1 : 2, new SecretKeySpec(array3, this.x), new IvParameterSpec(array, 0, this.getBlockSize()));
        }
        catch (final InvalidKeyException ex) {
            throw new IOException("Invalid encryption key");
        }
        catch (final InvalidAlgorithmParameterException ex2) {
            throw new IOException("Invalid algorithm parameter");
        }
    }
    
    public int getBlockSize() {
        return this.u.getBlockSize();
    }
}
