package com.maverick.ssh.components.jce;

import com.maverick.ssh.SshException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import com.maverick.ssh.components.SshSecureRandomGenerator;

public class SecureRND implements SshSecureRandomGenerator
{
    SecureRandom b;
    
    public SecureRND() throws NoSuchAlgorithmException {
        this.b = JCEProvider.getSecureRandom();
    }
    
    public void nextBytes(final byte[] array) {
        this.b.nextBytes(array);
    }
    
    public void nextBytes(final byte[] array, final int n, final int n2) throws SshException {
        try {
            final byte[] array2 = new byte[n2];
            this.b.nextBytes(array2);
            System.arraycopy(array2, 0, array, n, n2);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new SshException("ArrayIndexOutOfBoundsException: Index " + n + " on actual array length " + array.length + " with len=" + n2, 5);
        }
    }
    
    public int nextInt() {
        return this.b.nextInt();
    }
}
