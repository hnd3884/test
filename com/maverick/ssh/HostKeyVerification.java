package com.maverick.ssh;

import com.maverick.ssh.components.SshPublicKey;

public interface HostKeyVerification
{
    boolean verifyHost(final String p0, final SshPublicKey p1) throws SshException;
}
