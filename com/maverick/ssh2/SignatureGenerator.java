package com.maverick.ssh2;

import com.maverick.ssh.SshException;
import com.maverick.ssh.components.SshPublicKey;

public interface SignatureGenerator
{
    byte[] sign(final SshPublicKey p0, final byte[] p1) throws SshException;
}
