package com.maverick.ssh.components;

import java.io.IOException;

public interface SshPrivateKey
{
    byte[] sign(final byte[] p0) throws IOException;
    
    String getAlgorithm();
}
