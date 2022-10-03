package com.adventnet.cli.ssh.sshv2;

import com.maverick.ssh.SshException;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.HostKeyVerification;

public class SshHostKeyVerification implements HostKeyVerification
{
    public boolean verifyHost(final String s, final SshPublicKey sshPublicKey) throws SshException {
        return true;
    }
}
