package com.maverick.ssh.message;

import com.maverick.ssh.SshException;

public interface SshMessageReader
{
    byte[] nextMessage() throws SshException;
    
    boolean isConnected();
}
