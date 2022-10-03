package com.maverick.ssh.components;

import com.maverick.ssh.SshException;

public interface SshSecureRandomGenerator
{
    void nextBytes(final byte[] p0);
    
    void nextBytes(final byte[] p0, final int p1, final int p2) throws SshException;
    
    int nextInt();
}
