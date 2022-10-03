package com.maverick.ssh2;

import com.maverick.ssh.ChannelOpenException;
import com.maverick.ssh.SshException;

public interface ChannelFactory
{
    String[] supportedChannelTypes();
    
    Ssh2Channel createChannel(final String p0, final byte[] p1) throws SshException, ChannelOpenException;
}
