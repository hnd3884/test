package com.maverick.ssh2;

import com.maverick.ssh.SshException;

public interface GlobalRequestHandler
{
    String[] supportedRequests();
    
    boolean processGlobalRequest(final GlobalRequest p0) throws SshException;
}
