package com.maverick.ssh.components;

import java.io.IOException;

public abstract class SshCipher
{
    String b;
    public static final int ENCRYPT_MODE = 0;
    public static final int DECRYPT_MODE = 1;
    
    public SshCipher(final String b) {
        this.b = b;
    }
    
    public String getAlgorithm() {
        return this.b;
    }
    
    public abstract int getBlockSize();
    
    public abstract void init(final int p0, final byte[] p1, final byte[] p2) throws IOException;
    
    public void transform(final byte[] array) throws IOException {
        this.transform(array, 0, array, 0, array.length);
    }
    
    public abstract void transform(final byte[] p0, final int p1, final byte[] p2, final int p3, final int p4) throws IOException;
}
