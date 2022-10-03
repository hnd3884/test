package com.maverick.ssh.components;

import com.maverick.ssh.SshException;

public interface SshPublicKey
{
    void init(final byte[] p0, final int p1, final int p2) throws SshException;
    
    String getAlgorithm();
    
    int getBitLength();
    
    byte[] getEncoded() throws SshException;
    
    String getFingerprint() throws SshException;
    
    boolean verifySignature(final byte[] p0, final byte[] p1) throws SshException;
}
