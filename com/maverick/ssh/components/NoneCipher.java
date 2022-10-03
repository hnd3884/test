package com.maverick.ssh.components;

import java.io.IOException;

public class NoneCipher extends SshCipher
{
    public NoneCipher() {
        super("none");
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public void init(final int n, final byte[] array, final byte[] array2) throws IOException {
    }
    
    public void transform(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws IOException {
    }
}
