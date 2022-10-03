package com.adventnet.cli.ssh.sshv2;

import com.sshtools.j2ssh.transport.TransportProtocolException;
import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.TransportProtocolClient;

public class TransportProtocolClientExt extends TransportProtocolClient
{
    public TransportProtocolClientExt(final HostKeyVerification hostKeyVerification) throws TransportProtocolException {
        super(hostKeyVerification);
    }
    
    public void exitRun() {
        this.messageStore.close();
    }
}
