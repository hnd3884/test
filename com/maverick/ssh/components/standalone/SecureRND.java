package com.maverick.ssh.components.standalone;

import com.maverick.crypto.security.SecureRandom;
import com.maverick.ssh.components.SshSecureRandomGenerator;

public class SecureRND implements SshSecureRandomGenerator
{
    static SecureRND c;
    
    public static SecureRND getInstance() {
        return (SecureRND.c == null) ? (SecureRND.c = new SecureRND()) : SecureRND.c;
    }
    
    public void nextBytes(final byte[] array) {
        SecureRandom.getInstance().nextBytes(array);
    }
    
    public void nextBytes(final byte[] array, final int n, final int n2) {
        SecureRandom.getInstance().nextBytes(array, n, n2);
    }
    
    public int nextInt() {
        return SecureRandom.getInstance().nextInt();
    }
}
