package com.maverick.ssh.components;

import com.maverick.ssh.SshException;

public interface SshHmac
{
    int getMacLength();
    
    void generate(final long p0, final byte[] p1, final int p2, final int p3, final byte[] p4, final int p5);
    
    void init(final byte[] p0) throws SshException;
    
    boolean verify(final long p0, final byte[] p1, final int p2, final int p3, final byte[] p4, final int p5);
    
    void update(final byte[] p0);
    
    byte[] doFinal();
    
    String getAlgorithm();
}
